package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationRepo extends JpaRepository<VerificationEntity, Long> {
    @Query(value = "SELECT * FROM user_verification_data WHERE customer_id =:identityPhoneNumber ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<VerificationEntity> getUserVerificationData(@Param("identityPhoneNumber") String phoneNumber);
}
