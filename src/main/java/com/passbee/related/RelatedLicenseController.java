package com.passbee.related;

import com.passbee.related.dto.RelatedLicensesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
public class RelatedLicenseController {

    private final RelatedLicenseService service;

    @GetMapping("/{jmcd}/related")
    public RelatedLicensesResponse getRelated(
            @PathVariable String jmcd,
            @RequestParam(defaultValue = "similar") String type,
            @RequestParam(defaultValue = "10") int limit
    ){
        return service.getRelated(jmcd, type, limit);
    }
}
