package com.jihai.bitfree.lock;

import com.jihai.bitfree.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 避免每个地方都去写分布式锁的加解锁代码
 */
@Component
public class LockTemplateSupport {

    @Autowired
    private DistributedLock distributedLock;

    public void lock(String key, Integer expire, TimeUnit timeUnit, Runnable runnable) {
        Boolean locked = distributedLock.lock(key, expire, timeUnit);
        if (! locked) throw new BusinessException("请稍后再操作");
        try {
            runnable.run();
        } finally {
            distributedLock.unlock(key);
        }
    }
}
