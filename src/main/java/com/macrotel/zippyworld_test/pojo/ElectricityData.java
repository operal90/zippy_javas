package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

@Data
public class ElectricityData {
    @NotEmpty(message = "Phone Number cannot be empty")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    private String phonenumber;
    @NotEmpty(message = "Provide Meter Card")
    private String card_identity;
    @NotEmpty(message = "Provide an Amount")
    @Pattern(regexp = AMOUNT_VALIDATION_REGEX, message = "Amount can only be in digit")
    private String amount;
    @NotEmpty(message = "Provide Request Parameters ( channel)")
    private String channel;
    @NotEmpty(message = "Provide Account Type")
    private String account_type_id;
    @NotEmpty(message = "Provide Operator")
    private String operator_id;
    @NotEmpty(message = "Provide Buyer Phone number")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    private String buyer_phonenumber;
    @NotEmpty(message = "Invalid Customer Name")
    private String customer_name;
    @NotEmpty(message = "Provide Customer Address")
    private String customer_address;
    @NotEmpty(message = "Internal system error (token)")
    private String token;
    @NotEmpty(message = "Security Answer cannot be empty")
    private String security_answer;
    private String provider;
    private String provider_ref;
    private String buyer_email;
    //    @NotEmpty(message = "Transaction ID cannot be empty")
    //    private String transaction_id;
}
