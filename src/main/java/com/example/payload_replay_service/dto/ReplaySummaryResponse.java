package com.example.payload_replay_service.dto;

import java.time.Instant;

public record ReplaySummaryResponse(
        String executionId,
        String status,
        Long durationMs,
        Double successRate,
        Integer requestedTests,
        Integer executedTests,
        Integer successfulTests,
        Integer failedTests,
        Instant completedAt
) {
}
