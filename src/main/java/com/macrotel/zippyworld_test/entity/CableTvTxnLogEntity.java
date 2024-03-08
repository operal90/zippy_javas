package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "cable_tv_txn_logs")
public class CableTvTxnLogEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String customerId;
    private String cardIdentity;
    private String timeIn;
}
