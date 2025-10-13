package com.passbee.exam.controller;

import com.passbee.exam.ExamSubject;
import com.passbee.exam.ExamSubjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Exam Subjects (Query)", description = "시험 과목 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exam-subjects")
public class ExamQueryController {

    private final ExamSubjectRepository examSubjectRepository;

    @Operation(summary = "시험 과목 페이지 조회")
    @GetMapping
    public Page<ExamSubject> listSubjects(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        return examSubjectRepository.findAll(PageRequest.of(page, size));
    }
}


