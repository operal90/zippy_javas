package com.macrotel.zippyworld_test.dto;

import lombok.Data;

@Data
public class UserAccountDTO {
    private String firstname;
    private String lastname;
    private String phonenumber;
    private String email;
    private String user_type;
    private String user_package_id;
    private String is_tax_collector;
    private String commission_mode;
    private String kyc_level;
    private String bvn;
    private String bvn_verification_status;
    private String identityNumber;
    private String identity_verify_status;
    private String account_no;
    private String gtb_account_no;
    private String wtl_status;
    private String address;
    private String wtl_user_type;
    private String promo_code;
    private String referrer_code;

}
