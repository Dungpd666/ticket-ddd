package com.xxxx.ddd.application.service.ticketDetail.cache;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xxxx.ddd.application.model.cache.TicketDetailCache;
import com.xxxx.ddd.domain.exception.OrderNotAllowedException;
import com.xxxx.ddd.domain.model.entity.Order;
import com.xxxx.ddd.domain.model.entity.TicketDetail;
import com.xxxx.ddd.domain.model.enums.OrderStatus;
import com.xxxx.ddd.domain.respository.OrderRepository;
import com.xxxx.ddd.domain.service.TicketDetailDomainService;
import com.xxxx.ddd.infrastructure.cache.redis.RedisInfrasService;
import com.xxxx.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.xxxx.ddd.infrastructure.distributed.redisson.RedisDistributedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailCacheServiceRefactor {

    private final RedisDistributedService redisDistributedService;
    private final RedisInfrasService redisInfrasService;
    private final TicketDetailDomainService ticketDetailDomainService;
    private final OrderRepository orderRepository;

    // use guava
    private final static Cache<Long, TicketDetailCache> ticketDetailLocalCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .concurrencyLevel(12)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public boolean orderTicketByUser(Long ticketId, Long userId, int quantity) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(genEventItemKeyLock(ticketId));

        try {
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLock) {
                return false;
            }

            boolean decremented = ticketDetailDomainService.decrementStock(ticketId, quantity);
            if (!decremented) {
                return false;
            }

            Order order = new Order()
                    .setUserId(userId)
                    .setTicketDetailId(ticketId)
                    .setQuantity(quantity)
                    .setStatus(OrderStatus.PENDING)
                    .setCreatedAt(new Date());
            orderRepository.save(order);

            ticketDetailLocalCache.invalidate(ticketId);
            redisInfrasService.delete(genEventItemKey(ticketId));

            return true;
        } catch (OrderNotAllowedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    /**
     * get ticket item by id in cache
     */
    public TicketDetailCache getTicketDetail(Long ticketId, Long version) {
        // 1 - get data from local cache
        TicketDetailCache ticketDetailCache = getTicketDetailLocalCache(ticketId);

        if (ticketDetailCache != null) {

            // User:version, cache:version
            // 1. version = null
            if (version == null) {
                log.info("01: GET TICKET FROM LOCAL CACHE: versionUser:{}, versionLocal: {}", version,
                        ticketDetailCache.getVersion());
                return ticketDetailCache;
            }

            if (version.equals(ticketDetailCache.getVersion())) {
                log.info("02: GET TICKET FROM LOCAL CACHE: versionUser:{}, versionLocal: {}", version,
                        ticketDetailCache.getVersion());
                return ticketDetailCache;
            }

            // version < ticketDetailCache.getVersion()
            if (version < ticketDetailCache.getVersion()) {
                log.info("03: GET TICKET FROM LOCAL CACHE: versionUser:{}, versionLocal: {}", version,
                        ticketDetailCache.getVersion());
                return ticketDetailCache;
            }

            if (version > ticketDetailCache.getVersion()) {
                return getTicketDetailDistributedCache(ticketId);
            }
            // return ticketDetailCache;
        }
        return getTicketDetailDistributedCache(ticketId);
    }

    /**
     * get ticket from database
     */
    public TicketDetailCache getTicketDetailDatabase(Long ticketId) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(genEventItemKeyLock(ticketId));
        try {
            // 1 - Tao lock
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLock) {
                return null; // return retry
            }
            // Get cache
            TicketDetailCache ticketDetailCache = redisInfrasService.getObject(genEventItemKey(ticketId),
                    TicketDetailCache.class);
            // 2. YES
            if (ticketDetailCache != null) {
                return ticketDetailCache;
            }
            TicketDetail ticketDetail = ticketDetailDomainService.getTicketDetailById(ticketId);
            ticketDetailCache = new TicketDetailCache().withClone(ticketDetail).withVersion(System.currentTimeMillis());
            // set data to distributed cache
            redisInfrasService.setObject(genEventItemKey(ticketId), ticketDetailCache);
            return ticketDetailCache;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    /**
     * get ticket from distributed cache
     */
    public TicketDetailCache getTicketDetailDistributedCache(Long ticketId) {
        // 1 - get data
        TicketDetailCache ticketDetailCache = redisInfrasService.getObject(genEventItemKey(ticketId),
                TicketDetailCache.class);
        if (ticketDetailCache == null) {
            log.info("GET TICKET FROM DISTRIBUTED LOCK");
            ticketDetailCache = getTicketDetailDatabase(ticketId);
        }
        // 2 - put data to local cache
        // lock()
        ticketDetailLocalCache.put(ticketId, ticketDetailCache);
        // unLock()
        log.info("GET TICKET FROM DISTRIBUTED CACHE");
        return ticketDetailCache;
    }

    /**
     * get ticket from local cache
     */
    public TicketDetailCache getTicketDetailLocalCache(Long ticketId) {
        return ticketDetailLocalCache.getIfPresent(ticketId);
    }

    private String genEventItemKey(Long ticketId) {
        return "PRO_TICKET:ITEM:" + ticketId;
    }

    private String genEventItemKeyLock(Long ticketId) {
        return "PRO_LOCK_KEY_ITEM" + ticketId;
    }
}
