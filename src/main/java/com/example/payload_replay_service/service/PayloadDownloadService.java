package com.example.payload_replay_service.service;

import com.example.payload_replay_service.model.PayloadMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayloadDownloadService {

    private final S3Client s3Client;

    public String download(String bucket, String key) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes =
                s3Client.getObjectAsBytes(request);

        return objectBytes.asUtf8String();
    }

    public List<String> downloadBatch(String bucket, List<PayloadMetadata> payloads) {

        return payloads.stream()
                .map(payload -> download(bucket, payload.key()))
                .toList();
    }
}