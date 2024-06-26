package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "qr_customer_wallets")
public class QRCustomerWalletEntity implements Serializable {
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
    private String customerId;
    private Double walletBalance;
    private String userTypeId;
    private String status;
    private String operationAt;

    public QRCustomerWalletEntity() {
        this.operationAt = String.valueOf(LocalDate.now());
    }
}

