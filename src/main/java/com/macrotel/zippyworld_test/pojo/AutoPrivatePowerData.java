package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

@Data
public class AutoPrivatePowerData {
    @NotEmpty(message = "Phone number cannot be empty")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    private String phonenumber;
    @NotEmpty(message = "Provide Meter Card")
    private String card_identity;
    @NotEmpty(message = "Loading type cannot be empty")
    @Pattern(regexp = CHOICE_VALIDATION_REGEX, message = "Loading Type can either be 0 or 1, 1 for Load directly by the System and 0 for by User")
    private String loading_type;
    @NotEmpty(message = "Channel cannot be empty")
    private String channel;
    @NotEmpty(message = "Invalid customer name")
    private String customer_name;
    @NotEmpty(message = "Token cannot be empty")
    private String token;
    @NotEmpty(message = "Security answer cannot be empty")
    private String security_answer;
    @NotEmpty(message = "Amount cannot be empty")
    @Pattern(regexp =AMOUNT_VALIDATION_REGEX , message = "Amount can only be in digit")
    private String amount;
}
