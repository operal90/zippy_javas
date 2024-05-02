package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserAccountRepo extends JpaRepository<UserAccountEntity, Long> {
    Optional<UserAccountEntity>findByPhonenumber(String phoneNumber);
    Optional<UserAccountEntity> findByIdentityNumber(String userIdentityNumber);
    Optional<UserAccountEntity> findByEmail(String userEmailAddress);

    @Query(value = "SELECT u FROM UserAccountEntity u WHERE u.phonenumber =:phoneNumber AND u.answer =:securityAnswer")
    Optional<UserAccountEntity> confirmSecurityAnswer(@Param("phoneNumber") String phoneNumber, @Param("securityAnswer") String securityAnswer);

    @Query(value ="SELECT u FROM UserAccountEntity u WHERE u.phonenumber =:phoneNumber")
    Optional<UserAccountEntity> getUserAccountDetails(@Param("phoneNumber") String phoneNumber);

    @Query(value ="SELECT firstname,lastname,phonenumber,email,user_type,user_package_id , is_tax_collector , commission_mode , kyc_level,bvn , bvn_verification_status," +
            " identity_verify_status, account_no, IFNULL(gtb_account_no , '0') gtb_account_no, IFNULL(wtl_status , '2')  wtl_status,address, IFNULL(wtl_status , '2')  " +
            "wtl_user_type , promo_code, referrer_code FROM user_accounts WHERE u.phonenumber =:phoneNumber AND u.pin =:pin", nativeQuery = true)
    Optional<UserAccountEntity> authenticateUser(@Param("phoneNumber") String phoneNumber, @Param("pin") String pin);
}
