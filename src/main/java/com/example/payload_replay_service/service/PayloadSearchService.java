package com.example.payload_replay_service.service;

import com.example.payload_replay_service.config.CaptureProperties;
import com.example.payload_replay_service.model.PayloadMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayloadSearchService {

    private final S3Client s3Client;
    private final CaptureProperties captureProperties;

    /**
     * Permet de retourner la liste de payloads du bucket S3 par service
     * @param serviceName le nom du service
     * @return une liste de payloads du service
     */
    public List<PayloadMetadata> findByService(
            String serviceName) {

        String prefix =
                "service=" + serviceName + "/";

        ListObjectsV2Request request =
                ListObjectsV2Request.builder()
                        .bucket(captureProperties.bucketName())
                        .prefix(prefix)
                        .build();

        return s3Client.listObjectsV2(request)
                .contents()
                .stream()
                .map(object ->
                        new PayloadMetadata(
                                object.key(),
                                object.size(),
                                object.lastModified()))
                .toList();
    }

    /**
     * Permet de retourner les bons payloads du buckt3 selon les critères de recherche
     * @param service qu'on veut chercher
     * @param date qu'on veut chercher
     * @param method qu'on veut chercher
     * @param limit qu'on veut chercher
     * @return une liste de payloads du service
     */
    public List<PayloadMetadata> search(
            String service,
            String date,
            String method,
            int limit) {

        LocalDate localDate =
                LocalDate.parse(date);

        String prefix =
                String.format(
                        "service=%s/year=%s/month=%02d/day=%02d/",
                        service,
                        localDate.getYear(),
                        localDate.getMonthValue(),
                        localDate.getDayOfMonth());

        if (method != null) {
            prefix += "method=" + method + "/";
        }

        ListObjectsV2Request request =
                ListObjectsV2Request.builder()
                        .bucket(captureProperties.bucketName())
                        .prefix(prefix)
                        .build();

        ListObjectsV2Response response =
                s3Client.listObjectsV2(request);

        return response.contents()
                .stream()
                .limit(limit)
                .map(o -> new PayloadMetadata(
                        o.key(),
                        o.size(),
                        o.lastModified()))
                .toList();
    }
}