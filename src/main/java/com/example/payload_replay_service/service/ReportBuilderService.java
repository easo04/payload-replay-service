package com.example.payload_replay_service.service;

import com.example.payload_replay_service.model.ReplayResult;
import com.example.payload_replay_service.model.TestExecutionReport;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReportBuilderService {

    public TestExecutionReport build(
            String service,
            List<ReplayResult> results, String executionId, long duration) {

        int successCount =
                (int) results.stream()
                        .filter(ReplayResult::match)
                        .count();

        int failureCount =
                results.size() - successCount;

        double successRate =
                results.isEmpty()
                        ? 0
                        : ((double) successCount / results.size()) * 100;

        return new TestExecutionReport(
                executionId,
                Instant.now(),
                service,
                results.size(),
                successCount,
                failureCount,
                successRate,
                duration,
                results
        );
    }
}