package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.FundTransferTxnLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FundTransferTxnLogRepo extends JpaRepository<FundTransferTxnLogEntity,Long> {

    Optional<FundTransferTxnLogEntity> findByOperationId(String operationId);
}
