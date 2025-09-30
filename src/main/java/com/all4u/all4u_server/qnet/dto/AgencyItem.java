package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 15. 자격정보 인정 기관 정보 DTO
@Data
public class AgencyItem {
    @JacksonXmlProperty(localName = "rcogInstiNm")
    private String rcogInstiNm; // 경력인정 기관명

    @JacksonXmlProperty(localName = "crerRcogRate")
    private String crerRcogRate; // 경력인정 비율
}
