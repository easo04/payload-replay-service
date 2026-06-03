package com.example.payload_replay_service.service;

import com.example.payload_replay_service.model.TestExecutionReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    public void saveReport(
            String bucket,
            String key,
            TestExecutionReport report) {

        try {

            String json =
                    objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(report);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType("application/json")
                            .build(),
                    RequestBody.fromString(json));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String readReport(
            String bucket,
            String key) {

        return s3Client.getObjectAsBytes(
                        GetObjectRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .build())
                .asUtf8String();
    }

}