package com.example.payload_replay_service.service;

import com.example.payload_replay_service.config.CaptureProperties;
import com.example.payload_replay_service.model.ReportMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportSearchService {

    private final S3Client s3Client;
    private final CaptureProperties captureProperties;

    public List<ReportMetadata> findReports() {

        return s3Client.listObjectsV2(
                        ListObjectsV2Request.builder()
                                .bucket(captureProperties.bucketName())
                                .prefix("reports/")
                                .build())
                .contents()
                .stream()
                .map(object ->
                        new ReportMetadata(
                                object.key(),
                                object.size()))
                .toList();
    }
}