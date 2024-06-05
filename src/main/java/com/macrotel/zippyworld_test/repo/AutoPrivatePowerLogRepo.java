package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.AutoPrivatePowerLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AutoPrivatePowerLogRepo extends JpaRepository<AutoPrivatePowerLogEntity, Long> {

    @Query(value = "SELECT a FROM AutoPrivatePowerLogEntity a WHERE a.status='0' AND a.customerId =:customerId AND a.operationId =:referenceId")
    Optional<AutoPrivatePowerLogEntity> getEstatePowerTxnLog(@Param("customerId") String customerId, @Param("referenceId") String referenceId);
}
