package com.all4u.all4u_server.qnet.dto.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
@JacksonXmlRootElement(localName = "response")
public class QnetXmlBase<T> {
    private Header header;
    private Body<T> body;

    @Getter @Setter
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter @Setter
    public static class Body<T> {
        private Items<T> items;
    }

    @Getter @Setter
    public static class Items<T> {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<T> item;
    }
}
