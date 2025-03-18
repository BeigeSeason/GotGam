package com.springboot.gotgam.controller;

import com.springboot.gotgam.dto.LoginDto;
import com.springboot.gotgam.dto.Auth.MemberReqDto;
import com.springboot.gotgam.dto.Auth.SignupDto;
import com.springboot.gotgam.dto.Auth.TokenDto;
import com.springboot.gotgam.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Boolean> signup(@RequestBody SignupDto signupDto) {
        return ResponseEntity.ok(authService.signUp(signupDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    // 토큰 재발급
    @PostMapping("/token-refreshing")
    public ResponseEntity<String> refreshingAccessToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }

    // 내 정보 수정
    @PutMapping("/update")
    public ResponseEntity<Boolean> updateMember(@RequestBody MemberReqDto memberReqDto) {
        return ResponseEntity.ok(authService.updateMember(memberReqDto));
    }

    // 회원탈퇴
    @DeleteMapping("/sign-out")
    public ResponseEntity<Boolean> signOut(@RequestBody MemberReqDto memberReqDto) {
        return new ResponseEntity<>(authService.deleteMember(memberReqDto), HttpStatus.OK);
    }
}
