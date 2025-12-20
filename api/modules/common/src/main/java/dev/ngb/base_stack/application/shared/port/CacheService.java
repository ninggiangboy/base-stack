package dev.ngb.base_stack.application.shared.port;

import java.time.Duration;
import java.util.function.Supplier;

public interface CacheService {
    <V> void put(String key, V value);

    <V> void put(String key, V value, Duration timeout);

    <V> V get(String key, Supplier<V> callbackIfNull);

    <V> V get(String key, Supplier<V> callbackIfNull, Duration timeout);

    <V> V get(String key);

    void evict(String key);

    void evictAll(String prefix);
}
