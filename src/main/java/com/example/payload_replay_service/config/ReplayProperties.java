package com.example.payload_replay_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "replay")
public record ReplayProperties(
        String baselinePolicyServiceUrl,
        String candidatePolicyServiceUrl,
        Integer threadPoolSize
) {
}