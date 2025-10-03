package com.passbee.exam.controller;

import com.passbee.exam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService examService;

    @PostMapping("/import")
    public String importExamInfo(@RequestParam String jmcd,
                                 @RequestParam int baseYear) {
        examService.importExamInfo(jmcd, baseYear);
        return "saved: 1";
    }
}

