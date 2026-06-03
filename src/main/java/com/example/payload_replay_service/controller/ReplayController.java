package com.example.payload_replay_service.controller;

import com.example.payload_replay_service.model.ReplayRequest;
import com.example.payload_replay_service.service.ReplayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/replay")
@RequiredArgsConstructor
public class ReplayController {

    private final ReplayService replayService;

    @PostMapping
    public ResponseEntity<String> replay(@RequestBody ReplayRequest request)
            throws Exception {

        System.out.println(
                "Replay requested -> service="
                        + request.service()
                        + ", date="
                        + request.date()
                        + ", method="
                        + request.method()
                        + ", limit="
                        + request.limit()
        );

        replayService.replay(
                request.service(),
                request.date(),
                request.method(),
                request.limit()
        );

        // 202 = traitement accepté mais asynchrone/long running (plus correct pour replay)
        return ResponseEntity.accepted()
                .body("Replay started for service: " + request.service());
    }
}