package com.passbee.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 1. 연도별 응시자/합격자 수 DTO
@Data
public class TotalExamStatItem {
    @JacksonXmlProperty(localName = "pilrccnt1") private String pilrccnt1;
    @JacksonXmlProperty(localName = "pilrccnt2") private String pilrccnt2;
    @JacksonXmlProperty(localName = "pilrccnt3") private String pilrccnt3;
    @JacksonXmlProperty(localName = "pilrccnt4") private String pilrccnt4;
    @JacksonXmlProperty(localName = "pilrccnt5") private String pilrccnt5;
    @JacksonXmlProperty(localName = "pilrccnt6") private String pilrccnt6;
    @JacksonXmlProperty(localName = "silrccnt1") private String silrccnt1;
    @JacksonXmlProperty(localName = "silrccnt2") private String silrccnt2;
    @JacksonXmlProperty(localName = "silrccnt3") private String silrccnt3;
    @JacksonXmlProperty(localName = "silrccnt4") private String silrccnt4;
    @JacksonXmlProperty(localName = "silrccnt5") private String silrccnt5;
    @JacksonXmlProperty(localName = "silrccnt6") private String silrccnt6;
    @JacksonXmlProperty(localName = "pilpscnt1") private String pilpscnt1;
    @JacksonXmlProperty(localName = "pilpscnt2") private String pilpscnt2;
    @JacksonXmlProperty(localName = "pilpscnt3") private String pilpscnt3;
    @JacksonXmlProperty(localName = "pilpscnt4") private String pilpscnt4;
    @JacksonXmlProperty(localName = "pilpscnt5") private String pilpscnt5;
    @JacksonXmlProperty(localName = "pilpscnt6") private String pilpscnt6;
    @JacksonXmlProperty(localName = "silpacnt1") private String silpacnt1;
    @JacksonXmlProperty(localName = "silpacnt2") private String silpacnt2;
    @JacksonXmlProperty(localName = "silpacnt3") private String silpacnt3;
    @JacksonXmlProperty(localName = "silpacnt4") private String silpacnt4;
    @JacksonXmlProperty(localName = "silpacnt5") private String silpacnt5;
    @JacksonXmlProperty(localName = "silpacnt6") private String silpacnt6;
}
