package com.example.payload_replay_service.service;

import com.example.payload_replay_service.model.ReplayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReplayHttpClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Set<String> IGNORED_HEADERS =
            Set.of(
                    "host",
                    "content-length",
                    "connection",
                    "transfer-encoding");

    public ReplayResponse call(String url, String method, String payload, Map<String,String> headers) {

        HttpHeaders httpHeaders = new HttpHeaders();

        if(headers != null){

            //ignorer les headers qu'on ne veut pas passer
            headers.forEach((key,value) -> {

                if (!IGNORED_HEADERS.contains(key.toLowerCase())) {

                    httpHeaders.add(key,value);
                }
            });
        }

        HttpEntity<String> request =
                new HttpEntity<>(payload, httpHeaders);

        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());

        long start = System.currentTimeMillis();

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        httpMethod,
                        request,
                        String.class);

        long duration =
                System.currentTimeMillis() - start;

        return new ReplayResponse(
                response.getStatusCode().value(),
                duration,
                response.getBody());
    }
}