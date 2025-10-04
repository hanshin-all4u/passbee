package com.passbee.qnet.controller;

import com.passbee.qnet.service.QualificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Qualifications", description = "Q-Net 자격 종목 수집/조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qualifications")
public class QualificationController {

    private final QualificationService service;

    @Operation(summary = "Q-Net에서 페이지 수집 후 DB 저장")
    @GetMapping("/import")
    public String importPage(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size) throws Exception {
        int saved = service.importPage(page, size);
        return "saved: " + saved;
    }

}
