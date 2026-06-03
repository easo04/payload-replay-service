package com.example.payload_replay_service.model;

public record ReplayResponse(
        int statusCode,
        long responseTimeMs,
        String body
) {
}