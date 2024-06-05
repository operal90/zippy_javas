package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "pos_payment_notifications")
public class PosPaymentNotificationEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    private String billerReference;
    private String customerId;
    private String terminalId;
    private String transactionReference;
    private String reference;
    private Double amount;
    private String type;
    private String retrievalReferenceNumber;
    private String bank;
    private String transactionTime;
    private String maskedPan;
    private String cardScheme;
    private String customerName;
    private String statusCode;
    private String statusDescription;
    private String additionalInformation;
    private String currency;
    private String merchantId;
    private String stan;
    private String cardExpiry;
    private String cardHash;
    private String paymentDate;
    private String timeIn;
    private String requestDetails;
    private Double commission;
    private String stampDuty;
    private Double extraCharge;
    private Double credittedAmount;
    private String credittedStatus;
    private String credittedDt;

    public PosPaymentNotificationEntity() {
    }
}
