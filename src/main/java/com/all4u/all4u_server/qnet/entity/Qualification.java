package com.all4u.all4u_server.qnet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "qualification")
@Getter @Setter
@NoArgsConstructor
public class Qualification {

    @Id
    private String jmcd;            // PK

    private String jmfldnm;
    private String mdobligfldcd;
    private String mdobligfldnm;
    private String obligfldcd;
    private String obligfldnm;
    private String qualgbcd;
    private String qualgbnm;
    private String seriescd;
    private String seriesnm;
}

