package com.macrotel.zippyworld_test.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "request_logs")
public class RequestLoggingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String txnId;
    private String operation;
    private String operationId;
    private String requestParam;
    private String status;
    private String responseComplexMessage;
    private String responseActualMessage;
    private String timeIn;

    public RequestLoggingEntity() {
        this.timeIn = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));
    }
}
