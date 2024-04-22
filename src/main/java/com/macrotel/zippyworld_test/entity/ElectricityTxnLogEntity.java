package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "electric_txn_logs")
public class ElectricityTxnLogEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String customerId;
    private String cardIdentity;
    private String timeIn;
    private String operationId;
    private String txnId;
    private String channel;
    private String userTypeId;
    private String userPackageId;
    private String customerNames;
    private String customerAddress;
    private String operatorId;
    private double amount;
    private double commisionCharge;
    private double amountCharge;
    private String accountTypeId;
    private String provider;
    private String requestParam;
    private String status;
    private String token;
    private String responseComplexMessage;
    private String responseActualMessage;
    private String timeOut;

    public ElectricityTxnLogEntity() {
        this.timeIn = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
