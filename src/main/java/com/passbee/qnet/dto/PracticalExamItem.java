package com.passbee.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 14. 실기시험 지참물 정보 DTO
@Data
public class PracticalExamItem {
    @JacksonXmlProperty(localName = "jmNm")
    private String jmNm; // 종목명

    @JacksonXmlProperty(localName = "mtrlNm")
    private String mtrlNm; // 지참물 명

    @JacksonXmlProperty(localName = "mtrlExpl")
    private String mtrlExpl; // 규격
}
