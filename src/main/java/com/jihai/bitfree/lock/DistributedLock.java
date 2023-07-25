package com.jihai.bitfree.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    Boolean lock(String key, Integer expire, TimeUnit timeUnit);

    Boolean unlock(String key);
}
