package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "user_accounts")
public class UserAccountEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String operationId;
    private String firstname;
    private String lastname;
    private String phonenumber;
    private String gender;
    private String accountNo;
    private String promoCode;
    private String referrerCode;
    private String referralPhonenumber;
    private String identityId;
    private String identityNumber;
    private String status;
    private String wtlStatus;
    private String kycLevel;
    private String parentAggregatorCode;
    private String email;
    private String pin;
    private String txnPin;
    private String secureId;
    private String answer;
    private String userType;
    private String userPackageId;
    private String agreed;
    private String commissionMode;
    private String registeredAt;
    private String accountName;
    private String identityPhonenumber;
    private String pndStatus;
    private String aggregatorCode;

    public UserAccountEntity() {
        this.registeredAt = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));
    }
}
