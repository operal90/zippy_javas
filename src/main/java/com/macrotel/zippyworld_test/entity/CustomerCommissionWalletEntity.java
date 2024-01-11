package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "customer_commission_wallets")
public class CustomerCommissionWalletEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    private String referenceId;
    private String operationType;
    private String operationSummary;
    private Double amount;
    private String customerId;
    private Double walletBalance;
    private String operationDt;

    public CustomerCommissionWalletEntity(){
        this.operationDt = String.valueOf(LocalDate.now());
    }
}
