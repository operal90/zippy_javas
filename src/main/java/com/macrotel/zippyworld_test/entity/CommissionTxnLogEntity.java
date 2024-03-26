package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "commission_txn_logs")
public class CommissionTxnLogEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String operationId;
    private String txnId;
    private String serviceAmountNo;
    private String service;
    private String commissionType;
    private String customerId;
    private double amount;
    private String description;
    private String status;
    private String operationAt;

    public CommissionTxnLogEntity() {
        this.operationAt =String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd  HH:mm:ss")));
    }
}
