package com.springboot.gotgam.controller;

import com.springboot.gotgam.service.ImageProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image-proxy")
@Slf4j
public class ImageProxyController {

    private final ImageProxyService imageProxyService;

    // 이미지 프록시 엔드포인트
    @GetMapping
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        log.info("이미지 프록시 요청 받음: {}", url);
        try {
            ResponseEntity<byte[]> response = imageProxyService.proxyImage(url);
            log.info("이미지 프록시 응답 성공, 크기: {} bytes", response.getBody() != null ? response.getBody().length : 0);
            return response;
        } catch (Exception e) {
            log.error("이미지 프록시 처리 중 오류: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //CORS preflight 요청 처리
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        log.info("OPTIONS 요청 받음");
        return imageProxyService.handleOptionsRequest();
    }


}
