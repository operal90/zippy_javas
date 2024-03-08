package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.ElectricityTxnLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricityTxnLogRepo extends JpaRepository<ElectricityTxnLogEntity, Long> {
}
