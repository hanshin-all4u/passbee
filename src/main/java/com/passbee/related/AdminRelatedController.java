package com.passbee.related;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/related")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRelatedController {

    private final LicenseRelationRepository repo;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LicenseRelation create(@RequestBody CreateReq req){
        var e = LicenseRelation.builder()
                .srcJmcd(req.srcJmcd())
                .dstJmcd(req.dstJmcd())
                .type(RelationType.valueOf(req.type()))
                .weight(req.weight() == null ? BigDecimal.ONE : req.weight())
                .note(req.note())
                .build();
        return repo.save(e);
    }

    @PatchMapping("/{id}")
    public LicenseRelation update(@PathVariable Long id, @RequestBody UpdateReq req){
        var e = repo.findById(id).orElseThrow();
        if(req.weight()!=null) e.setWeight(req.weight());
        if(req.note()!=null) e.setNote(req.note());
        return repo.save(e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){ repo.deleteById(id); }

    public record CreateReq(
            @NotBlank String srcJmcd,
            @NotBlank String dstJmcd,
            @NotBlank String type,   // similar|higher|lower
            BigDecimal weight,
            String note
    ){}
    public record UpdateReq(BigDecimal weight, String note){}
}