package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "reversal_transactions")
public class ReversalTransactionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String reversalId;
    private String operationId;
    private String serviceAccountNo;
    private String customerId;
    private double amount;
    private String status;
    private String insertedDt;


    public ReversalTransactionEntity() {
        this.insertedDt = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));
    }
}
