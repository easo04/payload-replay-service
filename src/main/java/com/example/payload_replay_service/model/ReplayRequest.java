package com.example.payload_replay_service.model;

public record ReplayRequest(
        String service,
        String date,
        String method,
        Integer limit
) {
}