package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class QnetItems {
    @JacksonXmlProperty(localName = "item")
    @JacksonXmlElementWrapper(useWrapping = false) // item이 여러 번 반복됨
    private List<QualificationItem> item;
}
