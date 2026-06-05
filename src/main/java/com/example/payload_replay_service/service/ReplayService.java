package com.example.payload_replay_service.service;

import com.example.payload_replay_service.config.CaptureProperties;
import com.example.payload_replay_service.config.ReplayProperties;
import com.example.payload_replay_service.model.*;
import com.example.payload_replay_service.repository.ReplayExecutionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class ReplayService {

    private final PayloadSearchService searchService;
    private final PayloadDownloadService downloadService;
    private final ReportBuilderService reportBuilderService;
    private final ReportService reportService;
    private final ReplayHttpClient httpClient;
    private final CaptureProperties properties;
    private final ResponseComparator comparator;

    private final ObjectMapper objectMapper;
    private final ReplayProperties replayProperties;
    private final ExecutorService replayExecutor;
    private final ExecutionStore executionStore;

    private final ReplayExecutionRepository repository;

    public String replay(String service,
                       String date,
                       String method,
                       int limit) throws Exception {

        String executionId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        //récupérer les payloads dans S3
        List<PayloadMetadata> keys =
                searchService.search(service, date, method, limit);

        List<String> payloads =
                downloadService.downloadBatch(
                        properties.bucketName(),
                        keys);

        List<ReplayResult> results =
                java.util.Collections.synchronizedList(new ArrayList<>());

        List<Future<?>> futures = new ArrayList<>();

        //pour chaque payload, exécuter les requêtes et comparer
        for (String json : payloads) {

            futures.add(replayExecutor.submit(() -> {

                try {

                    System.out.println("Traitement payload: " + json);

                    CapturedPayload captured =
                            objectMapper.readValue(json, CapturedPayload.class);

                    String baselineUrl =
                            replayProperties.baselinePolicyServiceUrl()
                                    + "/"
                                    + captured.endpoint();

                    String candidateUrl =
                            replayProperties.candidatePolicyServiceUrl()
                                    + "/"
                                    + captured.endpoint();

                    //valider si c'est un GET
                    if (captured.queryString() != null
                            && !captured.queryString().isBlank()) {

                        baselineUrl += "?" + captured.queryString();
                        candidateUrl += "?" + captured.queryString();
                    }

                    System.out.println("Payload: " + captured.payload());

                    ReplayResponse baselineResponse =
                            httpClient.call(
                                    baselineUrl,
                                    captured.method(),
                                    captured.payload(),
                                    captured.headers());

                    ReplayResponse candidateResponse =
                            httpClient.call(
                                    candidateUrl,
                                    captured.method(),
                                    captured.payload(),
                                    captured.headers());

                    //comparer résultat des deux appels
                    ReplayResult result =
                            comparator.compare(
                                    captured.captureId(),
                                    captured.endpoint(),
                                    captured.method(),
                                    baselineResponse,
                                    candidateResponse);

                    results.add(result);

                } catch (Exception e) {
                    System.err.println("Erreur replay: " + e.getMessage());
                    e.printStackTrace();
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                //attends que tous les threads aient fini
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        long duration = System.currentTimeMillis() - startTime;

        //produire résultat final
        TestExecutionReport report =
                reportBuilderService.build(service, results, executionId, duration);

        //sauvegarder le raport dans le bucket s3
        saveReport(report, service, executionId);

        //sauvegarder le sommaire du rapport dans la BD
        saveReplayExecution(report, executionId);

        System.out.println("TOTAL TIME D'EXÉCUTION: " + duration + "ms");

        return executionId;
    }

    private void saveReport(TestExecutionReport report, String service, String executionId){
        String reportDate = LocalDate.now().toString();

        String reportKey = String.format(
                "reports/%s/%s/%s/report.json",
                reportDate,
                service,
                executionId
        );

        //sauvegarder en mémoire l'exécution
        executionStore.save(
                executionId,
                report
        );

        //enregistrer le rapport dans S3
        reportService.saveReport(
                properties.bucketName(),
                reportKey,
                report
        );
    }

    private void saveReplayExecution(TestExecutionReport report, String executionId) {
        ReplayExecutionEntity entity =
                new ReplayExecutionEntity();

        entity.setExecutionId(executionId);
        entity.setCreatedAtEpoch(Instant.now().toEpochMilli());
        entity.setStatus(report.failureCount() == 0
                        ? "SUCCESS"
                        : "PARTIAL_SUCCESS");

        entity.setDurationMs(report.duration());
        entity.setSuccessRate(report.successRate());
        entity.setRequestedTests(report.totalTests());
        entity.setExecutedTests(report.totalTests());
        entity.setSuccessfulTests(report.successCount());
        entity.setFailedTests(report.failureCount());
        entity.setCompletedAt(Instant.now().toString());

        repository.save(entity);
    }
}