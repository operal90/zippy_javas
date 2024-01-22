package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class IdentityData {
    @NotEmpty(message = "Identity Name cannot be empty")
    private String identityName;
}
