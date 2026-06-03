package com.example.payload_replay_service.model;

import java.time.Instant;

public record ReplayExecutionContext(
        String executionId,
        String service,
        Instant startTime,
        String status
) {}