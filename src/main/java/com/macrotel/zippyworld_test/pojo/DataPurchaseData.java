package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.AMOUNT_VALIDATION_REGEX;
import static com.macrotel.zippyworld_test.config.AppConstants.PHONE_NUMBER_VALIDATION_REGEX;

@Data
public class DataPurchaseData {
    @NotEmpty(message = "Phone Number cannot be empty")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    private String phonenumber;
    @NotEmpty(message = "Provide Beneficiary Phonenumber")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Beneficiary Phone Number can only be 11 digit")
    private String beneficiary_phonenumber;
    @NotEmpty(message = "Channel cannot be empty")
    private String channel;
    @NotEmpty(message = "Plan code cannot be empty")
    private String plan_code;
    @NotEmpty(message = "Amount cannot be empty")
//    @Pattern(regexp =AMOUNT_VALIDATION_REGEX , message = "Amount can only be in digit")
    private String amount;
    @NotEmpty(message = "Service Code cannot be empty")
    private String service_code;
    @NotEmpty(message = "Network Code cannot be empty")
    private String network_code;
    @NotEmpty(message = "Token cannot be empty")
    private String token;
    private String operation_summary;
    @NotEmpty(message = "Security cannot be empty")
    private String security_answer;

}
