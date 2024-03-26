package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "service_wallets")
public class ServiceWalletEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String referenceId;
    private String operationSummary;
    private String userTypeId;
    private String userPackageId;
    private String serviceAccountNo;
    private String customerId;
    private String provider;
    private String operationType;
    private String commisionType;
    private String tax;
    private double amount;
    private double discountAmount;
    private double commisionCharge;
    private double amountCharge;
    private double walletBalance;
    private String operationAt;
}
