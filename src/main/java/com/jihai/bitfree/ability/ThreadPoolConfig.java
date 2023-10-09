package com.jihai.bitfree.ability;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {


    @Bean
    public ExecutorService statisticThreadPool() {
        return Executors.newSingleThreadExecutor();
    }
}
