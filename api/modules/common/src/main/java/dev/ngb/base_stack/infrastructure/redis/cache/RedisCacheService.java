package dev.ngb.base_stack.infrastructure.redis.cache;

import dev.ngb.base_stack.application.shared.port.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisCacheService implements CacheService {

    private final RedissonClient redissonClient;

    // Cache to prevent stampede
    private final ConcurrentMap<String, CompletableFuture<?>> futureCache = new ConcurrentHashMap<>();

    private static final long LOCK_WAIT_TIME_MS = 2000;
    private static final long LOCK_LEASE_TIME_MS = 3000;
    private static final long FUTURE_TIMEOUT_MS = 5000;

    private static final String CACHE_PREFIX = "cache:";
    private static final String LOCK_PREFIX = "lock:";

    private String buildCacheKey(String key) {
        return CACHE_PREFIX + key;
    }

    @Override
    public <V> void put(String key, V value) {
        put(key, value, null);
    }

    @Override
    public <V> void put(String key, V value, Duration timeout) {
        RBucket<V> bucket = redissonClient.getBucket(buildCacheKey(key));
        if (timeout == null) {
            bucket.set(value);
        } else {
            bucket.set(value, timeout);
        }
    }

    @Override
    public <V> V get(String key) {
        return get(key, null, null);
    }

    @Override
    public <V> V get(String key, Supplier<V> callbackIfNull) {
        return get(key, callbackIfNull, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(String key, Supplier<V> callbackIfNull, Duration timeout) {

        String realKey = buildCacheKey(key);
        RBucket<V> bucket = redissonClient.getBucket(realKey);

        V value = bucket.get();
        if (value != null || callbackIfNull == null) {
            return value;
        }

        String lockKey = LOCK_PREFIX + realKey;

        CompletableFuture<V> future = (CompletableFuture<V>) futureCache.computeIfAbsent(
                realKey,
                _ -> CompletableFuture.supplyAsync(() -> {
                    RLock lock = redissonClient.getLock(lockKey);
                    boolean locked = false;
                    try {
                        locked = lock.tryLock(
                                LOCK_WAIT_TIME_MS,
                                LOCK_LEASE_TIME_MS,
                                TimeUnit.MILLISECONDS
                        );

                        V result = bucket.get();
                        if (result == null && locked) {
                            log.info("Cache miss for key {}, loading from callback...", realKey);
                            result = callbackIfNull.get();
                            if (result != null) {
                                put(key, result, timeout);
                            }
                        }
                        return result;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    } finally {
                        try {
                            if (locked && lock.isHeldByCurrentThread()) {
                                lock.unlock();
                            }
                        } finally {
                            futureCache.remove(realKey);
                        }
                    }
                })
        );

        try {
            return future.get(FUTURE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.warn("Timeout while loading cache for key {}, fallback to redis value", realKey);
            return bucket.get();
        } catch (Exception e) {
            log.error("Error while loading cache for key {}", realKey, e);
            return null;
        }
    }

    @Override
    public void evict(String key) {
        redissonClient.getBucket(buildCacheKey(key)).delete();
    }

    @Override
    public void evictAll(String prefix) {
        String realPrefix = buildCacheKey(prefix);
        RKeys rKeys = redissonClient.getKeys();

        KeysScanOptions options = KeysScanOptions.defaults()
                .limit(1000)
                .pattern(realPrefix + "*");

        try {
            List<String> keys = new ArrayList<>();
            rKeys.getKeys(options).forEach(keys::add);

            if (!keys.isEmpty()) {
                rKeys.delete(keys.toArray(new String[0]));
            }
        } catch (Exception e) {
            log.error("Failed to evict cache with prefix {}", realPrefix, e);
        }
    }
}
