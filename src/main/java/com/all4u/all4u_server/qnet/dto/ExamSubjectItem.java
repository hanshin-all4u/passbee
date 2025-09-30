package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 13. 국가자격 시험교시 과목 정보 DTO
@Data
public class ExamSubjectItem {
    @JacksonXmlProperty(localName = "jmNm")
    private String jmNm; // 종목명

    @JacksonXmlProperty(localName = "kmNm")
    private String kmNm; // 과목명

    @JacksonXmlProperty(localName = "kmYn")
    private String kmYn; // 필수과목여부
}
