package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "customer_identity_records")
public class CustomerIdentityRecordEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String customerId;
    private String kycId;
    private String identityNumber;
    private String identityName;
    private String insertedDt;

    public CustomerIdentityRecordEntity() {
        this.insertedDt = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));
    }
}
