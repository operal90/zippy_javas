package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.PosPaymentNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PosPaymentNotificationRepo extends JpaRepository<PosPaymentNotificationEntity,Long> {
    @Query(value = "SELECT p FROM PosPaymentNotificationEntity p where p.customerId =:customerId AND p.transactionReference =:referenceId")
    Optional<PosPaymentNotificationEntity> customerTransactionReference(@Param("customerId") String customerId, @Param("referenceId") String referenceId);
}
