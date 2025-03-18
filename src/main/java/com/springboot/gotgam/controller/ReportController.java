package com.springboot.gotgam.controller;

import com.springboot.gotgam.dto.report.ReportReqDto;
import com.springboot.gotgam.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {
    private final ReportService reportService;

    // 신고하기
    @PostMapping("")
    public ResponseEntity<Boolean> insertReport(@RequestBody ReportReqDto reportReqDto) {
        return ResponseEntity.ok(reportService.insertReport(reportReqDto));
    }
}
