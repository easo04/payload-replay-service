package com.example.payload_replay_service.service;

import com.example.payload_replay_service.dto.ReplaySummaryResponse;
import com.example.payload_replay_service.repository.ReplayExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReplayQueryService {

    private final ReplayExecutionRepository repository;

    public ReplaySummaryResponse latestReplay() {

        var replay =
                repository.findLatest()
                        .orElseThrow();

        return new ReplaySummaryResponse(
                replay.getExecutionId(),
                replay.getStatus(),
                replay.getDurationMs(),
                replay.getSuccessRate(),
                replay.getRequestedTests(),
                replay.getExecutedTests(),
                replay.getSuccessfulTests(),
                replay.getFailedTests(),
                Instant.parse(
                        replay.getCompletedAt()
                )
        );
    }
}