package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class QualificationItem {

    @JacksonXmlProperty(localName = "jmcd")
    private String jmcd;

    @JacksonXmlProperty(localName = "jmfldnm")
    private String jmfldnm;

    @JacksonXmlProperty(localName = "mdobligfldcd")
    private String mdobligfldcd;

    @JacksonXmlProperty(localName = "mdobligfldnm")
    private String mdobligfldnm;

    @JacksonXmlProperty(localName = "obligfldcd")
    private String obligfldcd;

    @JacksonXmlProperty(localName = "obligfldnm")
    private String obligfldnm;

    @JacksonXmlProperty(localName = "qualgbcd")
    private String qualgbcd;

    @JacksonXmlProperty(localName = "qualgbnm")
    private String qualgbnm;

    @JacksonXmlProperty(localName = "seriescd")
    private String seriescd;

    @JacksonXmlProperty(localName = "seriesnm")
    private String seriesnm;
}


