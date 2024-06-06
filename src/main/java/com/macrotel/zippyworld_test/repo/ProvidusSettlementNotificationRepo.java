package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.ProvidusSettlementNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProvidusSettlementNotificationRepo extends JpaRepository<ProvidusSettlementNotificationEntity,Long> {
    @Query("SELECT p FROM ProvidusSettlementNotificationEntity p WHERE p.customerId =:customerId AND p.transactionId =:transactionId")
    Optional<ProvidusSettlementNotificationEntity> getCustomerTransaction(@Param("customerId") String customerId, @Param("transactionId") String transactionId);
}
