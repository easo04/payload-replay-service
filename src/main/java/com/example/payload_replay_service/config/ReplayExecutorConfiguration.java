package com.example.payload_replay_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ReplayExecutorConfiguration {

    @Bean
    public ExecutorService replayExecutor(
            ReplayProperties properties) {

        int poolSize = properties.threadPoolSize() != null
                ? properties.threadPoolSize()
                : 10; // fallback safe

        return Executors.newFixedThreadPool(poolSize);
    }
}