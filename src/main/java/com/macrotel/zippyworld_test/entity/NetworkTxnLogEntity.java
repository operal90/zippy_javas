package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table (name = "network_txn_logs")
public class NetworkTxnLogEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String operationId;
    private String txnId;
    private String channel;
    private String userTypeId;
    private String userPackageId;
    private Float amount;
    private String commissionCharge;
    private String amountCharge;
    private String serviceAccountNo;
    private String provider;
    private String requestParam;
    private String status;
    private String responseComplexMessage;
    private String responseActualMessage;
    private String customerId;
    private String recipientNo;
    private String timeIn;
    private String timeOut;

    public NetworkTxnLogEntity() {
        this.timeIn = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));
    }
}
