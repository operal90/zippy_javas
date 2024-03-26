package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "customer_wallets")
public class CustomerWalletEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String referenceId;
    private String operationEvent;
    private String operationType;
    private String serviceAccountNo;
    private String operationSummary;
    private Double amount;
    private Double amountCharge;
    private String commisionType;
    private Double commisionCharge;
    private String commisionMode;
    private String customerId;
    private Double walletBalance;
    private String userTypeId;
    private String userPackageId;
    private String status;
    private String operationAt;

    public CustomerWalletEntity() {
        this.operationAt =String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd  HH:mm:ss")));
    }
}
