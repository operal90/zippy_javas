package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
@Data
@Entity
@Table(name = "qr_details")
public class QRDetailsEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    private String customerId;
    private String status;
    private String tokenReceiver;

    public QRDetailsEntity() {
    }
}
