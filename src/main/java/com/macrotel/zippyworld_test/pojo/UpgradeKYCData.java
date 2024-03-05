package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.APPROVAL_VALIDATION_REGEX;
import static com.macrotel.zippyworld_test.config.AppConstants.PHONE_NUMBER_VALIDATION_REGEX;
@Data
public class UpgradeKYCData {
    @NotEmpty(message = "KYC Identity Number cannot be empty")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Identity Number can only be 11 digit")
    private String identityNumber;
    @NotEmpty(message = "Approval cannot be empty")
    @Pattern(regexp = APPROVAL_VALIDATION_REGEX, message = "Approval can only be 0 for Accepted, 1 for rejected")
    private String approval;
}
