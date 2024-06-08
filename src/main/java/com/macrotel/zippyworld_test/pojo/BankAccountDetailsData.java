package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.ACCOUNT_NUMBER_VALIDATION_REGEX;

@Data
public class BankAccountData {
    @NotEmpty(message = "Account Number cannot be empty")
    @Pattern(regexp = ACCOUNT_NUMBER_VALIDATION_REGEX, message = "Account Number can only be 10 digit")
    private String account_number;
    @NotEmpty(message = "Bank Code ")
}
