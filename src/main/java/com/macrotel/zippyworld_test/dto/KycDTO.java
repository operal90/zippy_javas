package com.macrotel.zippyworld_test.dto;

import lombok.Data;

@Data
public class KycDTO {
    private String customerId;
    private String identityType;
    private String identityNumber;
    private String customerName;
    private String dateCreated;
}
