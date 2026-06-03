package com.example.payload_replay_service.model;

import java.util.List;

public record ReplayResult(
        String captureId,
        String endpoint,
        String method,
        int baselineStatus,
        int candidateStatus,
        long baselineResponseTimeMs,
        long candidateResponseTimeMs,
        boolean match,
        double similarityScore,
        List<String> differences
) {
}