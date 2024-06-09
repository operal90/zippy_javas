package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

@Data
public class BankTransferData {
    @NotEmpty(message = "Provide Request Parameter (account_number)")
    @Pattern(regexp = ACCOUNT_NUMBER_VALIDATION_REGEX, message = "Account Number can only be 10 digit")
    private String account_number;
    @NotEmpty(message = "Provide Request Parameter (account_name)")
    private String account_name;
    @NotEmpty(message = "Provide Request Parameter (bank_code)")
    private String bank_code;
    @NotEmpty(message = "Provide an Amount")
    @Pattern(regexp = AMOUNT_VALIDATION_REGEX, message = "Amount can only be in digit")
    private String amount;
    @NotEmpty(message = "Phone Number cannot be empty")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    private String phonenumber;
    @NotEmpty(message = "Internal system error (token)")
    private String token;
    @NotEmpty(message = "Security Answer cannot be empty")
    private String security_answer;
    @NotEmpty(message = "Provide Request Parameters ( channel)")
    private String channel;
    private String sender_phonenumber;
    private String description;
}
