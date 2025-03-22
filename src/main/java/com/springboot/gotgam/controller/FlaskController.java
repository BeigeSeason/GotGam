package com.springboot.gotgam.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/flask")
public class FlaskController {
    private final RestTemplate restTemplate;

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommend(@RequestBody Map<String, Object> data) {
        try {
            log.info(data.toString());
            ResponseEntity<Map> response = restTemplate.postForEntity("http://43.200.238.105:5000/recommend", data, Map.class);
            log.info("Flask 응답: {}", response.getBody());
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            log.error("Flask 요청 실패: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "추천 요청 실패: " + e.getMessage()));
        }
    }
}