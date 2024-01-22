package com.macrotel.zippyworld_test.config;

public class AppConstants {
    public  static final String  ERROR_MESSAGE = "Unsuccessful";
    public  static final String  SUCCESS_MESSAGE = "Successful";
    public static final String  ERROR_STATUS_CODE ="1";
    public static final String SUCCESS_STATUS_CODE = "0";
    public  static final Object EMPTY_RESULT = new Object [0];
    public static final String PHONE_NUMBER_VALIDATION_REGEX = "^\\d{11}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()-[{}]:;'?/*~$^+=<>.]).{6,20}$";
    public static final String AMOUNT_VALIDATION_REGEX = "^[\\d,\\.]+$";

    public static final String PAYMENT_VALIDATION_REGEX = "^(?i)(bank transfer|card payment)$";

    public static final String WITHDRAWAL_VALIDATION_REGEX = "^(?i)(portfolio balance|investment balance)$";
    public static final String APPROVAL_VALIDATION_REGEX = "^(0|2)$";

    public static final String CHARACTER_VALIDATION_REGEX = "^[a-zA-Z ]{3,}(?: [a-zA-Z ]+){0,2}$";
    public static final String PIN_VALIDATION_REGEX = "^\\d{4}$";

    public  static final String BVN_NIN_VALIDATION_REGEX="^\\d{11}$";

}
