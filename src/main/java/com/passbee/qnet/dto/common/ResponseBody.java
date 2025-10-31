package com.passbee.qnet.dto.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseBody<T> {

    @JacksonXmlProperty(localName = "pageNo")
    private Integer pageNo;

    @JacksonXmlProperty(localName = "totalCount")
    private Integer totalCount;

    @JacksonXmlProperty(localName = "numOfRows")
    private Integer numOfRows;

    @JacksonXmlProperty(localName = "items")
    private ResponseItems<T> items;
}
