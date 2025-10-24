package com.passbee.license.dto;

import com.passbee.exam.PracticalItem;
import lombok.Builder;

@Builder
public record PracticalItemResponseDto(
        Long id,
        String implYy,     // 시행연도
        String implSeq,    // 시행회차
        String mtrlNm,     // 지참물 명
        String mtrlExpl    // 규격
) {
    public static PracticalItemResponseDto from(PracticalItem practicalItem) {
        return PracticalItemResponseDto.builder()
                .id(practicalItem.getId())
                .implYy(practicalItem.getImplYy())
                .implSeq(practicalItem.getImplSeq())
                .mtrlNm(practicalItem.getMtrlNm())
                .mtrlExpl(practicalItem.getMtrlExpl())
                .build();
    }
}