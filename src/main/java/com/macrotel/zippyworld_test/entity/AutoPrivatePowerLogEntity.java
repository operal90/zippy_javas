package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "estate_power_txn_logs")
public class AutoPrivatePowerLogEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String operationId;
    private String userTypeId;
    private String customerId;
    private double amount;
    private double commisionCharge;
    private double amountCharge;
    private String cardIdentity;
    private String customerName;
    private String status;
    private String estateCode;
    private String paidStatus;
    private String orderNo;
    private String responseComplexMessage;
    private String responseActualMessage;
    private String timeIn;

    public AutoPrivatePowerLogEntity() {
        this.paidStatus = "1";
        this.timeIn = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
