package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserAccountRepo extends JpaRepository<UserAccountEntity, Long> {
    Optional<UserAccountEntity>findByPhonenumber(String phoneNumber);
    Optional<UserAccountEntity> findByIdentityNumber(String userIdentityNumber);
    Optional<UserAccountEntity> findByEmail(String userEmailAddress);
}
