package com.all4u.all4u_server.qnet.controller;

import com.all4u.all4u_server.qnet.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qnet/stat")
public class StatController {
    private final StatService statService;

    @PostMapping("/import/total")
    public String importTotal(@RequestParam int baseYear) {
        int saved = statService.importTotal(baseYear);
        return "saved: " + saved;
    }

    @PostMapping("/import/grade-pi-exam")
    public String importGradPiExam(@RequestParam int baseYear) {
        int saved = statService.importGradPiExam(baseYear);
        return "saved: " + saved;
    }
}
