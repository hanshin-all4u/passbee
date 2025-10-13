package com.passbee.agency;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Agencies", description = "자격정보 인정 기관 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agencies")
public class AgencyController {

    private final AgencyRepository repository;
    private final AgencyService service;

    @Operation(summary = "Q-Net에서 기관 데이터 전체 수집")
    @PostMapping("/import")
    public String importAll(@RequestParam(defaultValue = "100") int size) throws InterruptedException {
        int saved = service.importAll(size);
        return "saved: " + saved;
    }

    @Operation(summary = "기관 목록 페이지 조회")
    @GetMapping
    public Page<Agency> list(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size) {
        return repository.findAll(PageRequest.of(page, size));
    }
}


