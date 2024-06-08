package com.macrotel.zippyworld_test.config;

public class AppConstants {
    public  static final String  ERROR_MESSAGE = "Unsuccessful";
    public  static final String  SUCCESS_MESSAGE = "Successful";
    public static final String  ERROR_STATUS_CODE ="1";
    public static final String SUCCESS_STATUS_CODE = "0";
    public  static final Object EMPTY_RESULT = new Object [0];
    public static final String PHONE_NUMBER_VALIDATION_REGEX = "^\\d{11}$";
    public static final String ACCOUNT_NUMBER_VALIDATION_REGEX = "^\\d{10}$";
    public static final String CHOICE_VALIDATION_REGEX = "^(0|1)$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()-[{}]:;'?/*~$^+=<>.]).{6,20}$";
//    public static final String AMOUNT_VALIDATION_REGEX = "^[\\d,\\.]+$";
    public static final String AMOUNT_VALIDATION_REGEX = "^\\d{1,3}(?:,\\d{3})*(?:\\.\\d+)?$|^\\d+(?:\\.\\d+)?$";
    public static final String PAYMENT_VALIDATION_REGEX = "^(?i)(bank transfer|card payment)$";
    public static final String WITHDRAWAL_VALIDATION_REGEX = "^(?i)(portfolio balance|investment balance)$";
    public static final String APPROVAL_VALIDATION_REGEX = "^(0|1)$";
    public static final String CHARACTER_VALIDATION_REGEX = "^[a-zA-Z ]{3,}(?: [a-zA-Z ]+){0,2}$";
    public static final String PIN_VALIDATION_REGEX = "^\\d{4}$";
    public static final String SINGLE_DIGIT_VALIDATION_REGEX = "^\\d{1}$";
    public  static final String BVN_NIN_VALIDATION_REGEX="^\\d{11}$";
    public static final String NUMBER_VALIDATION_REGEX = "^\\d+$";
    public static final String  END_POINT_TLS = "https://zippyworld.com.ng:8443/telcom_live_services/main/api/";
    public static final String  END_POINT_MS = "https://zippyworld.com.ng:8443/macrotel_service_live/api/";
    public static final String CLIENT_ID_TLS = "ZW5AWYOK-983-IOP";
    public static final String CLIENT_ID_MS = "ZW5HYOK-530-IMA";
    public static final String X_API_KEY_TLS = "3216724";
    public static final String X_API_KEY_MS = "3410024";
    public static final String ELECTRICITY_SERVICE_CODE = "ZWOC002";
    public static final String TELCOM_SERVICE_CODE = "ZWOC001";
    public static final String CABLE_TV_SERVICE_CODE = "ZWOC003";
    public static final String BANK_TRANSFER_SERVICE_CODE = "ZWOC004";
    public static final String SHAGO_HASHKEY = "55cab22fb332762c2fb1fa36c986662e7c4c9100773914f80493bae450bd0888";
    public static final String SHAGO_LIVE_BASE_URL = "https://shagopayments.com/api/live/b2b";
    public static final String NOTIFICATION_BASE_URL = "http://194.163.149.51:3001/notifications/notif";
    public static final String HES_LIVE_BASE_URL = "http://185.252.232.72/api/";
    public static final String PRIVATE_ESTATE_COMMISSION_COLLECTOR = "08034441414";
    public static final Integer PRIVATE_ESTATE_COMMISSION_AMOUNT = 40;

}
