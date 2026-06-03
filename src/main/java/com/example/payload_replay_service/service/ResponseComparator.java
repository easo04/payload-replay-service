package com.example.payload_replay_service.service;

import com.example.payload_replay_service.model.ReplayResponse;
import com.example.payload_replay_service.model.ReplayResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseComparator {

    private final JsonDiffService jsonDiffService;

    public ReplayResult compare(
            String captureId,
            String endpoint,
            String method,
            ReplayResponse baseline,
            ReplayResponse candidate) {

        List<String> differences =
                new ArrayList<>();

        boolean match =
                baseline.statusCode() == candidate.statusCode()
                        &&
                        jsonDiffService.isEquivalent(
                                baseline.body(),
                                candidate.body());

        if (!match) {
            differences.add(
                    "JSON_CONTENT_DIFFERENCE");
        }

        return new ReplayResult(
            captureId,
            endpoint,
            method,
            baseline.statusCode(),
            candidate.statusCode(),
            baseline.responseTimeMs(),
            candidate.responseTimeMs(),
            match,
            match ? 1.0 : 0.0,
            differences
        );
    }
}