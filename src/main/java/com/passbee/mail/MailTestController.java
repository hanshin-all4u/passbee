package com.passbee.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/mail")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MailTestController {

    private final MailService mailService;

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void test(@RequestParam String to) {
        mailService.send(to, "[PassBee] 메일 테스트", "테스트 발송 본문입니다.");
    }

    // 매핑 확인용 간단 핑 (권한 없이 확인하려면 주석 해제)
    // @GetMapping("/ping")
    // public String ping(){ return "ok"; }
}