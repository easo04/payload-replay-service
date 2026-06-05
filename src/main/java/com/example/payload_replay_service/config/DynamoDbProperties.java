package com.example.payload_replay_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.dynamodb")
public record DynamoDbProperties(
        String tableName
) {}
