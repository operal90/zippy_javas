package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.PHONE_NUMBER_VALIDATION_REGEX;

@Data
public class CustomerReferenceData {
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be 11 digit")
    @NotEmpty(message = "PhoneNumber cannot be empty")
    private String phonenumber;
    private String reference_id;
    private String start_date;
    private String end_date;
    private String operation_type;
    private String service_account_no;
}
