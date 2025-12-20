package dev.ngb.base_stack.infrastructure.redis.lock;


import dev.ngb.base_stack.application.shared.port.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockService implements LockService {

    private final RedissonClient redissonClient;
    private final ThreadLocal<Map<String, RLock>> locksHolder = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private static final String LOCK_PREFIX = "lock:";

    private String buildLockKey(String key) {
        return LOCK_PREFIX + key;
    }

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime) {
        String realKey = buildLockKey(key);
        RLock lock = redissonClient.getLock(realKey);

        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (acquired) {
                locksHolder.get().put(realKey, lock);
            }
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread interrupted while trying to acquire lock for key: {}", key);
            return false;
        } catch (Exception e) {
            log.error("Error while trying to acquire lock for key: {}", key, e);
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        String realKey = buildLockKey(key);
        Map<String, RLock> threadLocks = locksHolder.get();
        RLock lock = threadLocks.get(realKey);

        if (lock != null && lock.isHeldByCurrentThread()) {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.warn("Attempted to unlock a lock not held by current thread for key: {}", key, e);
            } finally {
                threadLocks.remove(realKey);
                if (threadLocks.isEmpty()) {
                    locksHolder.remove();
                }
            }
        } else {
            log.warn("No lock held by current thread for key: {}", key);
        }
    }
}

