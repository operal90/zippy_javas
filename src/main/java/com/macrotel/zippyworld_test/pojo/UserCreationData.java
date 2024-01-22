package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

@Data
public class UserCreationData {
    private String promo_code;
    private String referrer_code;
    @NotEmpty(message = "Provide Firstname")
    @Pattern(regexp = CHARACTER_VALIDATION_REGEX, message = "Firstname must be at least 3 character")
    private String firstname;
    @NotEmpty(message = "Provide Lastname")
    @Pattern(regexp = CHARACTER_VALIDATION_REGEX, message = "Lastname must be at least 3 character")
    private String lastname;
    @NotEmpty(message = "Provide Phone number")
    @Pattern(regexp = PHONE_NUMBER_VALIDATION_REGEX, message = "Phone Number can only be in digit")
    private String phonenumber;
    @NotEmpty(message = "Provide email address")
    @Email(message = "Enter a valid email address")
    private String email;
    @NotEmpty(message = "Provide an Identity Id")
    private String identityId;
    @NotEmpty(message = "Provide an Identity Number")
    private String identityNumber;
    @NotEmpty(message = "Provide PIN")
    @Pattern(regexp = PIN_VALIDATION_REGEX, message = "PIN can only be 4 digit")
    private String pin;
    @NotEmpty(message = "Invalid Question id")
    private String uuid;
    @NotEmpty(message = "Answer cannot be empty")
    private String answer;
    @NotEmpty(message = "Invalid Request Parameters (transaction id)")
    private String transaction_id;
    private String secure_id;
    private String referral_phonenumber;
    private String aggregator_code;
    private String agreed;
    private String gender;

}
