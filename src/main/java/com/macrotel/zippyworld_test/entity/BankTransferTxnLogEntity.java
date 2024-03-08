package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "bank_transfer_txn_logs")
public class BankTransferTxnLogEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String customerId;
    private String accountNo;
    private String timeIn;

}
