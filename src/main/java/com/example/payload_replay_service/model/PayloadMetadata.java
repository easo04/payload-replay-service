package com.example.payload_replay_service.model;

import java.time.Instant;

public record PayloadMetadata(
        String key,
        Long size,
        Instant lastModified
) {
}