package com.passbee.qnet.dto.exam;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;
@Getter @Setter
public class ExamSubjectItem {

    @JacksonXmlProperty(localName = "implYy")
    private String implYy;

    @JacksonXmlProperty(localName = "examPckd")
    private String examPckd;

    @JacksonXmlProperty(localName = "docRegStartDt")
    private Date docRegStartDt;

    @JacksonXmlProperty(localName = "docRegEndDt")
    private Date docRegEndDt;

    @JacksonXmlProperty(localName = "docExamStartDt")
    private Date docExamStartDt;

    @JacksonXmlProperty(localName = "docPassDt")
    private Date docPassDt;

    @JacksonXmlProperty(localName = "fee")
    private String fee;

    @JacksonXmlProperty(localName = "acceptCdNm")
    private String acceptCdNm;

    @JacksonXmlProperty(localName = "etc")
    private String etc;
}
