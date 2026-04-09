# Middle SE Implementation Plan

Target: Solve the real distributed system problems that appear at 20,000 req/s.
Each feature here requires reasoning about failure scenarios, not just the happy path.

---

## Feature 1: Cache Warming on Startup

**Goal:** Prevent cold-start cache stampede after a restart. Pre-populate Redis with all active `TicketDetail` records before the app starts accepting traffic.

**Why it matters:** Without warming, the first wave of requests after a restart all miss local cache and Redis simultaneously, creating a thundering herd that can crash MySQL.

**Files to create:**
- `xxxx-application/.../service/ticket/cache/TicketDetailCacheWarmingService.java`

**Files to modify:**
- `xxxx-domain/.../domain/respository/TicketDetailRepository.java` — add `findAllActive`
- `xxxx-infrastructure/.../persistence/mapper/TicketDetailJPAMapper.java` — add query
- `xxxx-infrastructure/.../persistence/repository/TicketDetailInfrasRepositoryImpl.java` — implement

**Step 1 — Add `findAllActive` to repository:**

```java
// TicketDetailRepository (domain)
List<TicketDetail> findAllActive();
```

```java
// TicketDetailJPAMapper (infrastructure)
@Query("SELECT t FROM TicketDetail t WHERE t.status = 1")
List<TicketDetail> findAllActive();
```

**Step 2 — Create warming service:**

```java
// xxxx-application
@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailCacheWarmingService implements ApplicationListener<ApplicationReadyEvent> {

    private final TicketDetailDomainService ticketDetailDomainService;
    private final RedisInfrasService redisInfrasService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Cache warming started...");
        try {
            List<TicketDetail> activeTickets = ticketDetailDomainService.getAllActive();
            for (TicketDetail ticket : activeTickets) {
                TicketDetailCache cache = new TicketDetailCache()
                    .withClone(ticket)
                    .withVersion(System.currentTimeMillis());
                redisInfrasService.setObject("PRO_TICKET:ITEM:" + ticket.getId(), cache);
            }
            log.info("Cache warming completed: {} tickets loaded", activeTickets.size());
        } catch (Exception e) {
            // Log but do NOT rethrow — warming failure should not prevent startup
            log.error("Cache warming failed, continuing startup", e);
        }
    }
}
```

**Failure scenarios to handle:**
- Redis is down at startup → catch exception, log warning, continue (app still works, just cold)
- DB query returns 0 rows → valid state, nothing to warm
- App restarts mid-traffic → warming runs, but old requests may still hit DB briefly — this is acceptable

**Verification:**
1. Flush Redis: `redis-cli -p 6319 FLUSHALL`
2. Restart app
3. Check Redis: `redis-cli -p 6319 KEYS "PRO_TICKET:ITEM:*"` — keys should exist before first request

---

## Feature 2: Idempotency for Order Placement

**Goal:** Prevent a user from placing duplicate orders for the same ticket within a short window (e.g., double-click, network retry).

**Why it matters:** Without idempotency, a retry on a failed HTTP request can result in two DB rows and double stock deduction. The Redisson lock alone does not prevent this — it only serializes concurrent access, not repeated sequential calls.

**Approach:** Redis key `ORDER:IDEM:{userId}:{ticketDetailId}` with a short TTL. Set it atomically before acquiring the order lock. If it already exists, reject.

**Files to modify:**
- `xxxx-application/.../service/ticket/cache/TicketDetailCacheServiceRefactor.java`

**Files to create:**
- `xxxx-application/.../model/enums/OrderResult.java` — replace `boolean` return type

**Step 1 — Define a proper result type:**

```java
// xxxx-application
public enum OrderResult {
    SUCCESS,
    OUT_OF_STOCK,
    DUPLICATE_ORDER,
    LOCK_FAILED,
    SYSTEM_ERROR
}
```

**Step 2 — Add idempotency check to `orderTicketByUser`:**

```java
// TicketDetailCacheServiceRefactor
private static final long IDEMPOTENCY_TTL_SECONDS = 5;

public OrderResult orderTicketByUser(Long ticketDetailId, Long userId) {

    // 1. Idempotency check — fast path, no lock needed
    String idemKey = "ORDER:IDEM:" + userId + ":" + ticketDetailId;
    boolean isFirstRequest = redisInfrasService.setIfAbsent(idemKey, "1", IDEMPOTENCY_TTL_SECONDS);
    if (!isFirstRequest) {
        log.warn("Duplicate order rejected: userId={}, ticketDetailId={}", userId, ticketDetailId);
        return OrderResult.DUPLICATE_ORDER;
    }

    // 2. Acquire distributed lock
    RedisDistributedLocker locker = redisDistributedService.getDistributedLock(genEventItemKeyLock(ticketDetailId));
    try {
        boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
        if (!isLock) {
            redisInfrasService.delete(idemKey); // release idempotency key so user can retry
            return OrderResult.LOCK_FAILED;
        }

        // 3. Deduct stock
        boolean decremented = ticketDetailDomainService.decrementStock(ticketDetailId);
        if (!decremented) {
            redisInfrasService.delete(idemKey);
            return OrderResult.OUT_OF_STOCK;
        }

        // 4. Persist order
        Order order = new Order()
            .setUserId(userId)
            .setTicketDetailId(ticketDetailId)
            .setQuantity(1)
            .setStatus(0)
            .setCreatedAt(new Date())
            .setUpdatedAt(new Date());
        orderRepository.save(order);

        // 5. Invalidate caches
        ticketDetailLocalCache.invalidate(ticketDetailId);
        redisInfrasService.delete(genEventItemKey(ticketDetailId));

        return OrderResult.SUCCESS;

    } catch (Exception e) {
        redisInfrasService.delete(idemKey); // allow retry on system error
        log.error("Order failed: userId={}, ticketDetailId={}", userId, ticketDetailId, e);
        return OrderResult.SYSTEM_ERROR;
    } finally {
        locker.unlock();
    }
}
```

**Add `setIfAbsent` to `RedisInfrasService`:**

```java
// RedisInfrasService interface
boolean setIfAbsent(String key, String value, long ttlSeconds);

// RedisInfrasServiceImpl
@Override
public boolean setIfAbsent(String key, String value, long ttlSeconds) {
    return Boolean.TRUE.equals(
        redisTemplate.opsForValue().setIfAbsent(key, value, ttlSeconds, TimeUnit.SECONDS)
    );
}
```

**Failure scenarios to handle:**

| Scenario | Behavior |
|---|---|
| User double-clicks | Second request hits `setIfAbsent` → returns false → `DUPLICATE_ORDER` |
| Network retry after lock failure | `idemKey` deleted on lock failure → retry is allowed |
| Network retry after stock deducted | `idemKey` still exists → `DUPLICATE_ORDER` (stock not double-deducted) |
| Redis down | `setIfAbsent` throws → falls through to `SYSTEM_ERROR`, order not placed |
| App restarts mid-TTL | `idemKey` in Redis survives restart → still protects for remaining TTL |

**Verification:**

```bash
# Fire two requests simultaneously
for i in 1 2; do
  curl -X POST http://localhost:1122/ticket/1/detail/1/order \
    -H "Content-Type: application/json" \
    -d '{"userId": 42}' &
done
wait
# Expected: one SUCCESS, one DUPLICATE_ORDER
```

---

## Feature 3: Redis Inventory (Stock in Redis)

**Goal:** Move the stock decrement hotspot out of MySQL and into Redis using an atomic counter. At 20k req/s, every order hitting MySQL for a `SELECT + UPDATE` will exhaust the HikariCP pool (currently max 20 connections).

**Approach:**
1. On startup (via cache warming), write stock to Redis: `INVEN:TICKET:{id}` = `stockAvailable`
2. On order: `DECR` atomically in Redis. If result < 0, `INCR` back and reject.
3. Async consumer (or scheduled job) syncs Redis stock back to MySQL periodically.

**Files to create:**
- `xxxx-infrastructure/.../cache/redis/RedisInventoryService.java`

**Files to modify:**
- `xxxx-application/.../service/ticket/cache/TicketDetailCacheWarmingService.java` — also warm inventory counters
- `xxxx-application/.../service/ticket/cache/TicketDetailCacheServiceRefactor.java` — replace DB decrement with Redis DECR

**Step 1 — Inventory service in infrastructure:**

```java
// xxxx-infrastructure
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisInventoryService {

    private final StringRedisTemplate stringRedisTemplate;

    private String inventoryKey(Long ticketDetailId) {
        return "INVEN:TICKET:" + ticketDetailId;
    }

    public void initStock(Long ticketDetailId, int stockAvailable) {
        stringRedisTemplate.opsForValue().set(inventoryKey(ticketDetailId), String.valueOf(stockAvailable));
    }

    /**
     * Atomically deducts 1 from stock.
     * Returns true if successful (stock was > 0 before deduction).
     * Returns false if out of stock.
     */
    public boolean deductStock(Long ticketDetailId) {
        Long remaining = stringRedisTemplate.opsForValue().decrement(inventoryKey(ticketDetailId));
        if (remaining == null || remaining < 0) {
            // Roll back the decrement — key may not exist or stock was already 0
            stringRedisTemplate.opsForValue().increment(inventoryKey(ticketDetailId));
            return false;
        }
        return true;
    }

    public Long getStock(Long ticketDetailId) {
        String val = stringRedisTemplate.opsForValue().get(inventoryKey(ticketDetailId));
        return val == null ? null : Long.parseLong(val);
    }
}
```

**Step 2 — Add inventory warming to `TicketDetailCacheWarmingService`:**

```java
// In onApplicationEvent, after warming the read cache:
for (TicketDetail ticket : activeTickets) {
    redisInventoryService.initStock(ticket.getId(), ticket.getStockAvailable());
}
log.info("Inventory warming completed: {} counters initialized", activeTickets.size());
```

**Step 3 — Replace `ticketDetailDomainService.decrementStock` with Redis DECR in the order flow:**

```java
// TicketDetailCacheServiceRefactor — inside orderTicketByUser, after acquiring lock
boolean decremented = redisInventoryService.deductStock(ticketDetailId);
if (!decremented) {
    redisInfrasService.delete(idemKey);
    return OrderResult.OUT_OF_STOCK;
}
// DB write no longer happens in the hot path — see sync job below
```

**Step 4 — Async DB sync (scheduled):**

```java
// xxxx-application
@Component
@RequiredArgsConstructor
@Slf4j
public class InventorySyncJob {

    private final RedisInventoryService redisInventoryService;
    private final TicketDetailDomainService ticketDetailDomainService;
    private final TicketDetailRepository ticketDetailRepository;

    @Scheduled(fixedDelay = 5000) // every 5 seconds
    public void syncStockToDatabase() {
        // Get all active ticket IDs (from a known set or from DB)
        // For each, read Redis counter and update DB
        // This is eventually consistent — DB may lag Redis by up to 5s
        log.info("Syncing inventory from Redis to DB...");
        // Implementation depends on how you track active ticket IDs
        // Option: maintain a Redis SET "ACTIVE:TICKETS" populated during warming
    }
}
```

Add `@EnableScheduling` to `StartApplication`.

**Failure scenarios to handle:**

| Scenario | Behavior |
|---|---|
| Redis restarts | Inventory key gone → `DECR` on missing key returns -1 → all orders fail until re-warmed |
| Mitigation | On `deductStock`, if key missing → fall back to DB read, re-init counter, retry |
| DECR then app crashes before DB sync | Redis has correct count, DB is stale → sync job will fix on next run |
| DB sync fails | Log error, retry next cycle — Redis is authoritative for stock in this model |

**Verification:**

```bash
# Check Redis counter before and after an order
redis-cli -p 6319 GET "INVEN:TICKET:1"
# Place order
curl -X POST http://localhost:1122/ticket/1/detail/1/order \
  -H "Content-Type: application/json" -d '{"userId": 99}'
# Check counter again — should be decremented by 1
redis-cli -p 6319 GET "INVEN:TICKET:1"
```

---

## Feature 4: Kafka Async Order Flow

**Goal:** Decouple order placement from DB persistence. The HTTP call returns after Redis stock deduction. All DB writes happen asynchronously via Kafka, allowing the system to absorb traffic spikes without DB pressure.

**Architecture:**

```
POST /order
    → Idempotency check (Redis)
    → Deduct stock (Redis DECR)
    → Publish OrderCreatedEvent to Kafka topic "order.created"
    → Return SUCCESS immediately

Kafka Consumer (separate thread pool):
    → Consume OrderCreatedEvent
    → Persist Order to DB
    → Update ticket_item.stock_available in DB
    → (Future) Send confirmation notification
```

**Add Kafka dependency to root pom.xml:**

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

**Add Kafka config to `application.yml`:**

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all          # wait for all replicas — don't lose order events
      retries: 3
    consumer:
      group-id: order-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      auto-offset-reset: earliest
      properties:
        spring.json.trusted.packages: "com.xxxx.ddd.*"
```

**Files to create:**
- `xxxx-application/.../model/event/OrderCreatedEvent.java`
- `xxxx-application/.../service/order/OrderEventProducer.java`
- `xxxx-application/.../service/order/OrderEventConsumer.java`

**Event schema:**

```java
// xxxx-application
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private Long userId;
    private Long ticketDetailId;
    private int quantity;
    private long timestamp;
    private String eventId; // UUID for deduplication at consumer
}
```

**Producer:**

```java
// xxxx-application
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private static final String TOPIC = "order.created";
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderCreated(Long userId, Long ticketDetailId) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            userId, ticketDetailId, 1,
            System.currentTimeMillis(),
            UUID.randomUUID().toString()
        );
        kafkaTemplate.send(TOPIC, String.valueOf(ticketDetailId), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish OrderCreatedEvent: {}", event, ex);
                    // CRITICAL: stock was already deducted — must compensate or alert
                    // For now: log and let ops team investigate via Kibana
                } else {
                    log.info("OrderCreatedEvent published: offset={}", result.getRecordMetadata().offset());
                }
            });
    }
}
```

**Consumer:**

```java
// xxxx-application
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final OrderRepository orderRepository;
    private final RedisInfrasService redisInfrasService;

    @KafkaListener(topics = "order.created", groupId = "order-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Consuming OrderCreatedEvent: {}", event.getEventId());

        // Consumer-side idempotency — protect against Kafka at-least-once delivery
        String consumedKey = "KAFKA:CONSUMED:" + event.getEventId();
        boolean isNew = redisInfrasService.setIfAbsent(consumedKey, "1", 3600); // 1h TTL
        if (!isNew) {
            log.warn("Duplicate event skipped: {}", event.getEventId());
            return;
        }

        // Persist order
        Order order = new Order()
            .setUserId(event.getUserId())
            .setTicketDetailId(event.getTicketDetailId())
            .setQuantity(event.getQuantity())
            .setStatus(1) // confirmed
            .setCreatedAt(new Date(event.getTimestamp()))
            .setUpdatedAt(new Date());
        orderRepository.save(order);
    }
}
```

**Update `orderTicketByUser` to publish event instead of direct DB write:**

```java
// After Redis DECR succeeds:
orderEventProducer.publishOrderCreated(userId, ticketDetailId);
// Remove: orderRepository.save(order) from the hot path
return OrderResult.SUCCESS;
```

**Failure scenarios to handle:**

| Scenario | Behavior |
|---|---|
| Kafka broker down at publish time | Producer returns error in callback → log to Kibana, alert ops — stock deducted but order lost |
| Consumer crashes mid-processing | Kafka redelivers (at-least-once) → consumer-side idempotency via `KAFKA:CONSUMED:{eventId}` prevents duplicate orders |
| Consumer DB write fails | Do NOT commit Kafka offset → Kafka retries → eventually consistent |
| Event published twice (producer retry) | `eventId` UUID deduplicates at consumer |

**Verification:**

```bash
# Start Kafka (add to docker-compose or run locally)

# Place an order
curl -X POST http://localhost:1122/ticket/1/detail/1/order \
  -H "Content-Type: application/json" -d '{"userId": 42}'

# Check Kafka topic received event
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic order.created --from-beginning

# Check DB for persisted order (after consumer processes it)
mysql -h 127.0.0.1 -P 3316 -u root -proot1234 ticket \
  -e "SELECT * FROM orders ORDER BY id DESC LIMIT 5;"
```

---

## Implementation Order

```
1. Cache warming          — low risk, no failure modes that break existing flow
2. Idempotency            — must be in place before high-concurrency testing
3. Redis inventory        — replaces DB decrement, requires idempotency first
4. Kafka async flow       — replaces synchronous order persistence, requires #2 and #3
```

## Key Design Decisions to Understand

- **Redis is authoritative for stock** (features 3+4). MySQL becomes eventually consistent. This is an intentional trade-off for throughput.
- **Idempotency TTL is 5 seconds** — short enough to allow legitimate retries after a minute, long enough to catch double-clicks and network retries.
- **Producer errors (feature 4) are the hardest problem**: stock was deducted but the event was not published. This requires an outbox pattern or at-minimum alerting. Documenting the gap is more honest than pretending it doesn't exist.
- **Consumer idempotency is separate from producer idempotency** — Kafka's at-least-once delivery means even a correctly published event may be consumed twice.
