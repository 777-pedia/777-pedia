package org.example.pedia_777.domain.like.lock.distributed;

import java.util.function.Supplier;

public interface DistributedLockManager {

    <T> T executeWithLock(String key, Supplier<T> task) throws InterruptedException;
}
