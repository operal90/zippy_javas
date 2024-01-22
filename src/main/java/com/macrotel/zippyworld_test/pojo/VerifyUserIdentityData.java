package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.BVN_NIN_VALIDATION_REGEX;
import static com.macrotel.zippyworld_test.config.AppConstants.PHONE_NUMBER_VALIDATION_REGEX;

@Data
public class VerifyUserIdentityData {
    @NotEmpty(message = "Identity Id cannot be empty")
    private String identityId;
    @NotEmpty(message = "Identity Number cannot be empty")
    @Pattern(regexp = BVN_NIN_VALIDATION_REGEX, message = "Identity Number can only be 11 Digit")
    private String identityNumber;
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    @NotEmpty(message = "PhoneNumber cannot be empty")
    private String phoneNumber;
}
