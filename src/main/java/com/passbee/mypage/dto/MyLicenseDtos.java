package com.passbee.mypage.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.math.BigDecimal;

public class MyLicenseDtos {
    public record Create(
            @NotBlank String jmcd,
            @NotNull LocalDate obtainedDate,
            String levelGrade,
            @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal score,
            String certificateNo,
            String issuer,
            LocalDate expiresAt,
            @Size(max=255) String memo
    ) {}

    public record Update(
            String levelGrade,
            @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal score,
            String certificateNo,
            String issuer,
            LocalDate expiresAt,
            @Size(max=255) String memo
    ) {}

    public record Item(
            Long id,
            String jmcd,
            String name,           // jmcd → 이름 매핑 (없으면 jmcd 그대로)
            LocalDate obtainedDate,
            String levelGrade,
            BigDecimal score,
            String certificateNo,
            String issuer,
            LocalDate expiresAt,
            String memo
    ) {}
}
