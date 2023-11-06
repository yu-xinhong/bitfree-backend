package com.jihai.bitfree.support;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionUtils {

    /**
     * 事务后置处理，可以优化大事务提升连接池性能，尽量保证分布式事务一致性
     * @param runnable
     */
    public void doAfterTransaction(Runnable runnable) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new DoTransactionCompletion(runnable));
        }
    }
}
