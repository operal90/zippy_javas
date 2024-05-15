package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.PHONE_NUMBER_VALIDATION_REGEX;

@Data
public class CustomerQueryData {
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    @NotEmpty(message = "PhoneNumber cannot be empty")
    private String phonenumber;
    @NotEmpty(message = "Internal system error (token)")
    private String token;
    @NotEmpty(message = "Provide Request Parameters ( channel)")
    private String channel;
}
