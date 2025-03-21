package com.springboot.gotgam.controller;

import com.springboot.gotgam.dto.diary.DiaryReqDto;
import com.springboot.gotgam.dto.diary.DiaryResDto;
import com.springboot.gotgam.service.DiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping("/post-diary")
    public ResponseEntity<Boolean> postDiary(@RequestBody DiaryReqDto reqDto) {
        return new ResponseEntity<>(diaryService.createDiary(reqDto), HttpStatus.OK);
    }

    @PutMapping("/edit-diary")
    public ResponseEntity<Boolean> editDiary(@RequestBody DiaryReqDto reqDto) {
        return ResponseEntity.ok(diaryService.editDiary(reqDto));
    }

    @DeleteMapping("/delete/{diaryId}")
    public ResponseEntity<Boolean> deleteDiary(@PathVariable String diaryId) {
        return ResponseEntity.ok(diaryService.deleteDiary(diaryId));
    }

    // 다이어리 상세내용
    @GetMapping("/diary-detail/{diaryId}")
    public ResponseEntity<DiaryResDto> getDiaryDetail(@PathVariable String diaryId) {
        return ResponseEntity.ok(diaryService.getDiaryDetail(diaryId));
    }

    // 다이어리 공개/비공개 전환
    @PutMapping("/change-ispublic")
    public ResponseEntity<Boolean> changeIsPublic(@RequestParam String diaryId, @RequestParam boolean isPublic) {
        return ResponseEntity.ok(diaryService.changeIsPublic(diaryId, isPublic));
    }
}
