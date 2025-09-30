package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class QualitativeInfoItem {
    @JacksonXmlProperty(localName = "jmNm")
    private String jmNm; // 자격명

    @JacksonXmlProperty(localName = "summary")
    private String summary; // 개요

    @JacksonXmlProperty(localName = "job")
    private String job; // 수행직무

    @JacksonXmlProperty(localName = "career")
    private String career; // 진로 및 전망
}
