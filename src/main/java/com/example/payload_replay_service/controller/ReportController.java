package com.example.payload_replay_service.controller;

import com.example.payload_replay_service.config.CaptureProperties;
import com.example.payload_replay_service.model.ReportMetadata;
import com.example.payload_replay_service.service.ReportSearchService;
import com.example.payload_replay_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {

    private final ReportSearchService reportSearchService;
    private final ReportService reportService;
    private final CaptureProperties captureProperties;

    @GetMapping("/reports")
    public List<ReportMetadata> reports() {

        return reportSearchService.findReports();
    }

    @GetMapping("/reports/content")
    public String reportContent(
            @RequestParam String key) {

        return reportService.readReport(
                captureProperties.bucketName(),
                key);
    }
}