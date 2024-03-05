package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "aggregator_user_accounts")
public class AggregatorUserAccountEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    private String aggregatorCode;
    private String agentNumber;
    private String createdAt;

    public AggregatorUserAccountEntity() {
        this.createdAt = String.valueOf(LocalDate.now());
    }
}
