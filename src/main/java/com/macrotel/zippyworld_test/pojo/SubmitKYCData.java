package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.PHONE_NUMBER_VALIDATION_REGEX;
import static com.macrotel.zippyworld_test.config.AppConstants.SINGLE_DIGIT_VALIDATION_REGEX;

@Data
public class SubmitKYCData {
    @NotEmpty(message = "Customer ID cannot be empty")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Customer ID can only be 11 digit")
    private String customerId;
    @Pattern(regexp = SINGLE_DIGIT_VALIDATION_REGEX, message = "KYC Id can only be in number")
    @NotEmpty(message = "KYC Id cannot be empty")
    private String identityId;
    @NotEmpty(message = "KYC Identity Number cannot be empty")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Identity Number can only be 11 digit")
    private String identityNumber;
    @NotEmpty(message = "KYC Identity Name cannot be empty")
    private String identityName;
}
