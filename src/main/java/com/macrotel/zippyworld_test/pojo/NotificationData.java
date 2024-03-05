package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.PHONE_NUMBER_VALIDATION_REGEX;

@Data
public class NotificationData {
    @NotEmpty(message = "Phone Number cannot be empty")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    public String phoneNumber;
    public String emailAddress;
}
