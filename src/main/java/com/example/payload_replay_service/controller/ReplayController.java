package com.example.payload_replay_service.controller;

import com.example.payload_replay_service.model.ReplayRequest;
import com.example.payload_replay_service.service.ExecutionStore;
import com.example.payload_replay_service.service.ReplayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/replay")
@RequiredArgsConstructor
public class ReplayController {

    private final ReplayService replayService;
    private final ExecutionStore executionStore;

    @PostMapping
    public ResponseEntity<?> replay(@RequestBody ReplayRequest request)
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

        String executionId = replayService.replay(
                request.service(),
                request.date(),
                request.method(),
                request.limit()
        );

        return ResponseEntity.accepted().body(
                Map.of(
                        "executionId", executionId,
                        "status", "STARTED"
                )
        );
    }

    @GetMapping("/{executionId}")
    public Object getExecution(@PathVariable String executionId) {
        return executionStore.get(executionId);
    }
}