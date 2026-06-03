package com.example.payload_replay_service.model;

import java.time.Instant;
import java.util.List;

public record TestExecutionReport(
        String executionId,
        Instant executionDate,
        String service,
        int totalTests,
        int successCount,
        int failureCount,
        double successRate,
        List<ReplayResult> results
) {
}