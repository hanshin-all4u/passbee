package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 16. 거주지별 접수 현황 DTO
@Data
public class RegionalReceptionItem {
    @JacksonXmlProperty(localName = "abdAddr") private String abdAddr;
    @JacksonXmlProperty(localName = "brchNm") private String brchNm;
    @JacksonXmlProperty(localName = "implPlanNm") private String implPlanNm;
    @JacksonXmlProperty(localName = "jmFldNm") private String jmFldNm;
    @JacksonXmlProperty(localName = "recptCnt") private String recptCnt;
    @JacksonXmlProperty(localName = "seqNo") private String seqNo;
}
