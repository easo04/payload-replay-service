package com.example.payload_replay_service.service;

import com.example.payload_replay_service.config.CaptureProperties;
import com.example.payload_replay_service.config.ReplayProperties;
import com.example.payload_replay_service.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void replay(String service,
                       String date,
                       String method,
                       int limit) throws Exception {

        long start = System.currentTimeMillis();

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

        //produire résultat final
        TestExecutionReport report =
                reportBuilderService.build(service, results);

        String reportId = UUID.randomUUID().toString();
        String reportDate = LocalDate.now().toString();

        String reportKey = String.format(
                "reports/%s/%s-%s.json",
                reportDate,
                service,
                reportId
        );

        //enregistrer le rapport dans S3
        reportService.saveReport(
                properties.bucketName(),
                reportKey,
                report
        );

        long duration = System.currentTimeMillis() - start;
        System.out.println("TOTAL TIME D'EXÉCUTION: " + duration + "ms");
    }
}