package com.example.payload_replay_service.model;

import java.time.Instant;
import java.util.Map;

public record CapturedPayload(
        String captureId,
        Instant timestamp,
        String serviceName,
        String endpoint,
        String method,
        String queryString,
        Map<String, String> headers,
        String payload
) {
}