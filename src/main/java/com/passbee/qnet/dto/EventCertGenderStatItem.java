package com.passbee.qnet.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

// 8. 종목별 성별 현황 DTO
@Data
public class EventCertGenderStatItem {
    @JacksonXmlProperty(localName = "jmnm") private String jmnm;
    @JacksonXmlProperty(localName = "sexnm") private String sexnm;
    @JacksonXmlProperty(localName = "ilpcnt1") private String ilpcnt1;
    @JacksonXmlProperty(localName = "ilpcnt2") private String ilpcnt2;
    @JacksonXmlProperty(localName = "ilpcnt3") private String ilpcnt3;
    @JacksonXmlProperty(localName = "ilpcnt4") private String ilpcnt4;
    @JacksonXmlProperty(localName = "ilpcnt5") private String ilpcnt5;
    @JacksonXmlProperty(localName = "ilpcnt6") private String ilpcnt6;
}
