package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 10. 자격취득자 연령별 수 DTO
@Data
public class CertAgeStatItem {
    @JacksonXmlProperty(localName = "agenm") private String agenm;
    @JacksonXmlProperty(localName = "statisyy1") private String statisyy1;
    @JacksonXmlProperty(localName = "statisyy2") private String statisyy2;
    @JacksonXmlProperty(localName = "statisyy3") private String statisyy3;
    @JacksonXmlProperty(localName = "statisyy4") private String statisyy4;
    @JacksonXmlProperty(localName = "statisyy5") private String statisyy5;
    @JacksonXmlProperty(localName = "statisyy6") private String statisyy6;
}
