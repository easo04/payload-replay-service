package com.example.payload_replay_service.controller;

import com.example.payload_replay_service.model.PayloadMetadata;
import com.example.payload_replay_service.service.PayloadSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PayloadController {

    private final PayloadSearchService payloadSearchService;

    public PayloadController(
            PayloadSearchService searchService) {
        this.payloadSearchService = searchService;
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }

    @GetMapping("/payloads")
    public List<PayloadMetadata> findPayloads(
            @RequestParam String service) {

        System.out.println("CALL API PAYLOADS");

        return payloadSearchService.findByService(service);
    }

    @GetMapping("/payloads/search")
    public List<PayloadMetadata> search(
            @RequestParam String service,
            @RequestParam String date,
            @RequestParam(required = false) String method,
            @RequestParam(defaultValue = "100") int limit) {

        return payloadSearchService.search(
                service,
                date,
                method,
                limit);
    }
}