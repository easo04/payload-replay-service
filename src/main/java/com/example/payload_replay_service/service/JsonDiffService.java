package com.example.payload_replay_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JsonDiffService {

    private final ObjectMapper objectMapper;

    public boolean isEquivalent(
            String baselineResponse,
            String candidateResponse) {

        try {

            JsonNode baselineNode =
                    objectMapper.readTree(baselineResponse);

            JsonNode candidateNode =
                    objectMapper.readTree(candidateResponse);

            return baselineNode.equals(candidateNode);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erreur comparaison JSON",
                    e);
        }
    }
}