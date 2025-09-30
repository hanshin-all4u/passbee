package com.all4u.all4u_server.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 6, 7번 통계 API DTO
@Data
public class EventYearStatItem {
    @JacksonXmlProperty(localName = "jmnm") private String jmnm;
    @JacksonXmlProperty(localName = "ilrcnt1") private String ilrcnt1;
    @JacksonXmlProperty(localName = "ilrcnt2") private String ilrcnt2;
    @JacksonXmlProperty(localName = "ilrcnt3") private String ilrcnt3;
    @JacksonXmlProperty(localName = "ilrcnt4") private String ilrcnt4;
    @JacksonXmlProperty(localName = "ilrcnt5") private String ilrcnt5;
    @JacksonXmlProperty(localName = "ilrcnt6") private String ilrcnt6;
    @JacksonXmlProperty(localName = "ilecnt1") private String ilecnt1;
    @JacksonXmlProperty(localName = "ilecnt2") private String ilecnt2;
    @JacksonXmlProperty(localName = "ilecnt3") private String ilecnt3;
    @JacksonXmlProperty(localName = "ilecnt4") private String ilecnt4;
    @JacksonXmlProperty(localName = "ilecnt5") private String ilecnt5;
    @JacksonXmlProperty(localName = "ilecnt6") private String ilecnt6;
    @JacksonXmlProperty(localName = "ilpcnt1") private String ilpcnt1;
    @JacksonXmlProperty(localName = "ilpcnt2") private String ilpcnt2;
    @JacksonXmlProperty(localName = "ilpcnt3") private String ilpcnt3;
    @JacksonXmlProperty(localName = "ilpcnt4") private String ilpcnt4;
    @JacksonXmlProperty(localName = "ilpcnt5") private String ilpcnt5;
    @JacksonXmlProperty(localName = "ilpcnt6") private String ilpcnt6;
}
