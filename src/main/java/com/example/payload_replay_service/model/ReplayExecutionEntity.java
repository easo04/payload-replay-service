package com.example.payload_replay_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ReplayExecutionEntity {

    private String executionId;
    private Long createdAtEpoch;
    private String status;
    private Long durationMs;
    private Double successRate;
    private Integer requestedTests;
    private Integer executedTests;
    private Integer successfulTests;
    private Integer failedTests;
    private String completedAt;

    @DynamoDbPartitionKey
    public String getExecutionId() {
        return executionId;
    }
}