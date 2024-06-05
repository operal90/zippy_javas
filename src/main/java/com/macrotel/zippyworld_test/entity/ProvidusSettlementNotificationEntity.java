package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "providus_settlement_notifications")
public class ProvidusSettlementNotificationEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    private String transactionId;
    private String header;
    private String body;
    private String sourceAccountName;
    private String sourceBankName;
    private String sourceAccountNumber;
    private String tranRemarks;
    private String sessionId;
    private String customerId;
    private String accountNo;
    private String primaryCustomerId;
    private String primaryAccountNo;
    private String initiationTranRef;
    private String app;
    private Double amount;
    private Double settledAmount;
    private String machine;
    private String insertedStatus;
    private String timeIn;
    private String credittedStatus;
    private String credittedDt;
    private String clientCallbackResponse;
    private String clientCallbackResponseDt;
    private String clientTransferResponse;
    private String clientTransferResponseDt;

    public ProvidusSettlementNotificationEntity() {
    }
}
