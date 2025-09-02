package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class QnetBody {
    @JacksonXmlProperty(localName = "items")
    private QnetItems items;
}
