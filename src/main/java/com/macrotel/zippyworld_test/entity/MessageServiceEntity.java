package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "message_services")
public class MessageServiceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    private String customerId;
    private String email;
    private String sms;
    private String whatsapp;

    public MessageServiceEntity(){

    }
}
