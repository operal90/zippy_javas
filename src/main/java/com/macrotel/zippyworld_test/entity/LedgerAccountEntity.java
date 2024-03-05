package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "ledger_accounts")
public class LedgerAccountEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String referenceId;
    private String operationType;
    private String serviceAccountNo;
    private String operationSummary;
    private String amount;
    private String customerId;
    private String channel;
    private String operationAt;
}
