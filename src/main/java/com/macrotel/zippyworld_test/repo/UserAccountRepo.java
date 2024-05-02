package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserAccountRepo extends JpaRepository<UserAccountEntity, Long> {
    Optional<UserAccountEntity>findByPhonenumber(String phoneNumber);
    Optional<UserAccountEntity> findByIdentityNumber(String userIdentityNumber);
    Optional<UserAccountEntity> findByEmail(String userEmailAddress);

    @Query(value = "SELECT u FROM UserAccountEntity u WHERE u.phonenumber =:phoneNumber AND u.answer =:securityAnswer")
    Optional<UserAccountEntity> confirmSecurityAnswer(@Param("phoneNumber") String phoneNumber, @Param("securityAnswer") String securityAnswer);

    @Query(value ="SELECT u FROM UserAccountEntity u WHERE u.phonenumber =:phoneNumber")
    Optional<UserAccountEntity> getUserAccountDetails(@Param("phoneNumber") String phoneNumber);

    @Query(value ="SELECT u FROM UserAccountEntity u WHERE u.phonenumber =:phoneNumber AND u.pin =:pin")
    Optional<UserAccountEntity> authenticateUser(@Param("phoneNumber") String phoneNumber, @Param("pin") String pin);
}
