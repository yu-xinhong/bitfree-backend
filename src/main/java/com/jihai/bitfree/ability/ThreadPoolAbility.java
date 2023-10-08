package com.jihai.bitfree.ability;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ThreadPoolAbility {

    public ExecutorService statisticThreadPool = Executors.newSingleThreadExecutor();

    public ExecutorService getStatisticThreadPool() {
        return statisticThreadPool;
    }
}
