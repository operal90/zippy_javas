package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.BankTransferTxnLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransferTxnRepo extends JpaRepository<BankTransferTxnLogEntity, Long> {
}
