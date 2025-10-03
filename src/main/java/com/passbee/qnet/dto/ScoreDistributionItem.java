package com.passbee.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 17. 원서 점수 분포 DTO
@Data
public class ScoreDistributionItem {
    @JacksonXmlProperty(localName = "comCdNm") private String comCdNm;
    @JacksonXmlProperty(localName = "grdNm") private String grdNm;
    @JacksonXmlProperty(localName = "implSeq") private String implSeq;
    @JacksonXmlProperty(localName = "implYy") private String implYy;
    @JacksonXmlProperty(localName = "jmFldNm") private String jmFldNm;
    @JacksonXmlProperty(localName = "kmNm") private String kmNm;
    @JacksonXmlProperty(localName = "kmPnt40") private String kmPnt40;
    @JacksonXmlProperty(localName = "kmPnt50") private String kmPnt50;
    @JacksonXmlProperty(localName = "kmPnt60") private String kmPnt60;
    @JacksonXmlProperty(localName = "kmPnt70") private String kmPnt70;
    @JacksonXmlProperty(localName = "kmPnt80") private String kmPnt80;
    @JacksonXmlProperty(localName = "kmPnt81") private String kmPnt81;
    @JacksonXmlProperty(localName = "kmPntAvg") private String kmPntAvg;
}
