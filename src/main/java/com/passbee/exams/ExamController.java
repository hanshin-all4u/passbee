package com.passbee.exams;

import com.passbee.exams.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Tag(name = "Exams")
public class ExamController {
    private final ExamService service;

    @GetMapping @Operation(summary="시험 목록", description="q 로 제목/기관 검색")
    public Map<String,Object> list(@RequestParam(required=false) String q){ return Map.of("items", service.list(q)); }

    @GetMapping("/{id}") @Operation(summary="시험 상세")
    public ExamDetailDto detail(@PathVariable Long id){ return service.get(id); }

    @PostMapping @Operation(summary="시험 생성")
    public ResponseEntity<ExamDetailDto> create(@RequestBody @Valid ExamCreateRequest req){ return ResponseEntity.ok(service.create(req)); }

    @PutMapping("/{id}") @Operation(summary="시험 수정")
    public ExamDetailDto update(@PathVariable Long id, @RequestBody @Valid ExamUpdateRequest req){ return service.update(id, req); }

    @DeleteMapping("/{id}") @Operation(summary="시험 삭제")
    public ResponseEntity<Void> delete(@PathVariable Long id){ service.delete(id); return ResponseEntity.noContent().build(); }
}
