# Junior SE Implementation Plan

Target: Complete the core order flow end-to-end, following existing DDD patterns.

---

## Feature 1: Global Exception Handler

**Goal:** All unhandled exceptions return a structured `ResultMessage<Void>` instead of a raw 500 stack trace.

**Files to create:**
- `xxxx-controller/src/main/java/com/xxxx/ddd/controller/advice/GlobalExceptionHandler.java`

**Files to read first:**
- `xxxx-controller/.../model/vo/ResultMessage.java`
- `xxxx-controller/.../model/enums/ResultCode.java`
- `xxxx-controller/.../model/enums/ResultUtil.java`

**Implementation:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultMessage<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ResultUtil.error(ResultCode.PARAMS_ERROR, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultMessage<Void> handleRuntime(RuntimeException ex) {
        return ResultUtil.error(ResultCode.UN_ERROR, ex.getMessage());
    }
}
```

**Add to `ResultCode` enum** (if not present):
```
PARAMS_ERROR(400, "Invalid parameters"),
UN_ERROR(500, "System error")
```

**Verification:** Call `GET /ticket/999/detail/999` with a non-existent ID. Before: raw 500. After: structured JSON error.

---

## Feature 2: Order Domain Entity + Repository

**Goal:** Persist an order record when a user buys a ticket.

**Files to create:**
- `xxxx-domain/.../domain/model/entity/Order.java`
- `xxxx-domain/.../domain/respository/OrderRepository.java`
- `xxxx-infrastructure/.../persistence/mapper/OrderJPAMapper.java`
- `xxxx-infrastructure/.../persistence/repository/OrderInfrasRepositoryImpl.java`

**Domain entity:**

```java
// xxxx-domain
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long ticketDetailId;
    private int quantity;       // always 1 for now
    private int status;         // 0=pending, 1=confirmed, 2=cancelled
    private Date createdAt;
    private Date updatedAt;
}
```

**Repository interface (domain layer):**

```java
// xxxx-domain
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
}
```

**JPA mapper (infrastructure layer):**

```java
// xxxx-infrastructure
@Repository
public interface OrderJPAMapper extends JpaRepository<Order, Long> {
}
```

**Repository implementation (infrastructure layer):**

```java
// xxxx-infrastructure
@Service
@RequiredArgsConstructor
public class OrderInfrasRepositoryImpl implements OrderRepository {

    private final OrderJPAMapper orderJPAMapper;

    @Override
    public Order save(Order order) {
        return orderJPAMapper.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJPAMapper.findById(id);
    }
}
```

**SQL migration** (run manually against MySQL on port 3316):

```sql
CREATE TABLE orders (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    ticket_detail_id BIGINT  NOT NULL,
    quantity    INT          NOT NULL DEFAULT 1,
    status      INT          NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## Feature 3: Complete the Order Flow (Stock Decrement + Order Persistence)

**Goal:** `orderTicketByUser` must actually deduct stock in DB and create an Order record inside the existing Redisson lock.

**Files to modify:**
- `xxxx-domain/.../domain/service/TicketDetailDomainService.java` — add `decrementStock`
- `xxxx-domain/.../domain/service/impl/TicketDetailDomainServiceImpl.java` — implement it
- `xxxx-domain/.../domain/respository/TicketDetailRepository.java` — add `save`
- `xxxx-infrastructure/.../persistence/repository/TicketDetailInfrasRepositoryImpl.java` — implement `save`
- `xxxx-application/.../service/ticket/cache/TicketDetailCacheServiceRefactor.java` — wire it all together
- `xxxx-controller/.../http/TicketDetailController.java` — change to POST, add userId

**Step 1 — Add `save` to TicketDetailRepository:**

```java
// xxxx-domain
public interface TicketDetailRepository {
    Optional<TicketDetail> findById(Long id);
    TicketDetail save(TicketDetail ticketDetail);  // ADD THIS
}
```

Implement in `TicketDetailInfrasRepositoryImpl`:

```java
@Override
public TicketDetail save(TicketDetail ticketDetail) {
    return ticketDetailJPAMapper.save(ticketDetail);
}
```

**Step 2 — Add `decrementStock` to domain service:**

```java
// TicketDetailDomainService
boolean decrementStock(Long ticketDetailId);
```

```java
// TicketDetailDomainServiceImpl
@Override
@Transactional
public boolean decrementStock(Long ticketDetailId) {
    TicketDetail detail = ticketDetailRepository.findById(ticketDetailId).orElse(null);
    if (detail == null || detail.getStockAvailable() <= 0) {
        return false;
    }
    detail.setStockAvailable(detail.getStockAvailable() - 1);
    detail.setUpdatedAt(new Date());
    ticketDetailRepository.save(detail);
    return true;
}
```

**Step 3 — Wire order creation into `orderTicketByUser`:**

```java
// TicketDetailCacheServiceRefactor — update orderTicketByUser
public boolean orderTicketByUser(Long ticketDetailId, Long userId) {
    RedisDistributedLocker locker = redisDistributedService.getDistributedLock(genEventItemKeyLock(ticketDetailId));
    try {
        boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
        if (!isLock) {
            return false;
        }

        // 1. Deduct stock in DB
        boolean decremented = ticketDetailDomainService.decrementStock(ticketDetailId);
        if (!decremented) {
            return false; // out of stock
        }

        // 2. Persist order record
        Order order = new Order()
            .setUserId(userId)
            .setTicketDetailId(ticketDetailId)
            .setQuantity(1)
            .setStatus(0)
            .setCreatedAt(new Date())
            .setUpdatedAt(new Date());
        orderRepository.save(order);

        // 3. Invalidate caches
        ticketDetailLocalCache.invalidate(ticketDetailId);
        redisInfrasService.delete(genEventItemKey(ticketDetailId));

        return true;
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
        locker.unlock();
    }
}
```

**Step 4 — Update controller:**

```java
// Change from GET to POST, add userId
@PostMapping("/{ticketId}/detail/{detailId}/order")
public ResultMessage<Boolean> orderTicketByUser(
    @PathVariable("ticketId") Long ticketId,
    @PathVariable("detailId") Long detailId,
    @RequestParam("userId") Long userId
) {
    boolean result = ticketDetailAppService.orderTicketByUser(detailId, userId);
    return result ? ResultUtil.data(true) : ResultUtil.error(ResultCode.UN_ERROR, "Order failed");
}
```

---

## Feature 4: Request Validation

**Goal:** Reject bad input at the controller boundary before it reaches business logic.

**Add to root pom.xml:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**Create an order request DTO:**

```java
// xxxx-controller
@Data
public class OrderRequest {
    @NotNull(message = "userId is required")
    @Min(value = 1, message = "userId must be positive")
    private Long userId;
}
```

**Update controller to use it:**

```java
@PostMapping("/{ticketId}/detail/{detailId}/order")
public ResultMessage<Boolean> orderTicketByUser(
    @PathVariable Long ticketId,
    @PathVariable Long detailId,
    @RequestBody @Valid OrderRequest request
) {
    boolean result = ticketDetailAppService.orderTicketByUser(detailId, request.getUserId());
    return result ? ResultUtil.data(true) : ResultUtil.error(ResultCode.UN_ERROR, "Order failed");
}
```

**Add `MethodArgumentNotValidException` handler to `GlobalExceptionHandler`:**

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ResultMessage<Void> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ResultUtil.error(ResultCode.PARAMS_ERROR, message);
}
```

---

## Implementation Order

```
1. GlobalExceptionHandler         — isolated, zero risk, immediate value
2. Order entity + repository      — no business logic yet, pure structure
3. Complete order flow            — depends on #2, touches all layers
4. Request validation             — polish, depends on #3 controller shape
```

## Testing Each Step

```bash
# 1. Test exception handler
curl http://localhost:1122/ticket/999/detail/999

# 2. Test order (after completing flow)
curl -X POST http://localhost:1122/ticket/1/detail/1/order \
  -H "Content-Type: application/json" \
  -d '{"userId": 42}'

# 3. Verify stock decremented in DB
mysql -h 127.0.0.1 -P 3316 -u root -proot1234 ticket \
  -e "SELECT stock_available FROM ticket_item WHERE id = 1;"

# 4. Verify order record created
mysql -h 127.0.0.1 -P 3316 -u root -proot1234 ticket \
  -e "SELECT * FROM orders ORDER BY id DESC LIMIT 1;"
```
