package com.all4u.all4u_server.qnet.controller;

import com.all4u.all4u_server.qnet.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qnet/stat")
public class StatController {
    private final StatService statService;

    @PostMapping("/total/{baseYear}")
    public String importTotal(@PathVariable int baseYear) {
        int saved = statService.importTotal(baseYear);
        return "saved: " + saved;
    }
}