package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.CommissionTxnLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionTxnLogRepo extends JpaRepository<CommissionTxnLogEntity, Long> {
}
