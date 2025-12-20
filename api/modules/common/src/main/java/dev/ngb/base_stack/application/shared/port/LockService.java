package dev.ngb.base_stack.application.shared.port;

public interface LockService {
    boolean tryLock(String key, long waitTime, long leaseTime);

    void unlock(String key);
}
