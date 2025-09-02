package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "response")
public class QnetResponse {

    @JacksonXmlProperty(localName = "header")
    private QnetHeader header;

    @JacksonXmlProperty(localName = "body")
    private QnetBody body;
}
