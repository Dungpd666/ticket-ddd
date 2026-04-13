package com.xxxx.ddd.application.service.seatClass.cache;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xxxx.ddd.application.model.cache.SeatClassCache;
import com.xxxx.ddd.domain.exception.OrderNotAllowedException;
import com.xxxx.ddd.domain.model.entity.Order;
import com.xxxx.ddd.domain.model.entity.SeatClass;
import com.xxxx.ddd.domain.model.enums.OrderStatus;
import com.xxxx.ddd.domain.respository.OrderRepository;
import com.xxxx.ddd.domain.service.SeatClassDomainService;
import com.xxxx.ddd.infrastructure.cache.redis.RedisInfrasService;
import com.xxxx.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.xxxx.ddd.infrastructure.distributed.redisson.RedisDistributedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatClassCacheService {

    private final RedisDistributedService redisDistributedService;
    private final RedisInfrasService redisInfrasService;
    private final SeatClassDomainService seatClassDomainService;
    private final OrderRepository orderRepository;

    private static final Cache<Long, SeatClassCache> localCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .concurrencyLevel(12)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Transactional
    public boolean orderSeatClassByUser(Long seatClassId, Long userId, int quantity) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(lockKey(seatClassId));
        try {
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLock) {
                return false;
            }

            boolean decremented = seatClassDomainService.decrementStock(seatClassId, quantity);
            if (!decremented) {
                return false;
            }

            Order order = new Order()
                    .setUserId(userId)
                    .setTicketDetailId(seatClassId)
                    .setQuantity(quantity)
                    .setStatus(OrderStatus.PAYMENT_PENDING)
                    .setCreatedAt(new Date());
            orderRepository.save(order);

            localCache.invalidate(seatClassId);
            redisInfrasService.delete(cacheKey(seatClassId));

            return true;
        } catch (OrderNotAllowedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    public SeatClassCache getSeatClass(Long seatClassId, Long version) {
        SeatClassCache cached = localCache.getIfPresent(seatClassId);

        if (cached != null) {
            if (version == null || version <= cached.getVersion()) {
                log.info("GET SEAT CLASS FROM LOCAL CACHE: versionReq={}, versionLocal={}", version,
                        cached.getVersion());
                return cached;
            }
            return getFromDistributedCache(seatClassId);
        }
        return getFromDistributedCache(seatClassId);
    }

    private SeatClassCache getFromDistributedCache(Long seatClassId) {
        SeatClassCache cached = redisInfrasService.getObject(cacheKey(seatClassId), SeatClassCache.class);
        if (cached == null) {
            log.info("GET SEAT CLASS FROM DB VIA DISTRIBUTED LOCK");
            cached = getFromDatabase(seatClassId);
        }
        localCache.put(seatClassId, cached);
        log.info("GET SEAT CLASS FROM DISTRIBUTED CACHE");
        return cached;
    }

    private SeatClassCache getFromDatabase(Long seatClassId) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(lockKey(seatClassId));
        try {
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLock) {
                return null;
            }
            SeatClassCache cached = redisInfrasService.getObject(cacheKey(seatClassId), SeatClassCache.class);
            if (cached != null) {
                return cached;
            }
            SeatClass seatClass = seatClassDomainService.getSeatClassById(seatClassId);
            cached = new SeatClassCache().withClone(seatClass).withVersion(System.currentTimeMillis());
            redisInfrasService.setObject(cacheKey(seatClassId), cached);
            return cached;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    private String cacheKey(Long seatClassId) {
        return "TRAIN:SEAT_CLASS:" + seatClassId;
    }

    private String lockKey(Long seatClassId) {
        return "TRAIN:LOCK:SEAT_CLASS:" + seatClassId;
    }
}
