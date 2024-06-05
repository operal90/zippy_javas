package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "fund_transfer_txn_logs")
public class FundTransferTxnLogEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    private String operationId;
    private String txnId;
    private String userTypeId;
    private String customerId;
    private String recipient;
    private String senderName;
    private String recipientName;
    private Double amount;
    private String description;
    private String requestParam;
    private String status;
    private String responseComplexMessage;
    private String responseActualMessage;
    private String timeIn;
    private String timeOut;

    public FundTransferTxnLogEntity() {
    }
}
