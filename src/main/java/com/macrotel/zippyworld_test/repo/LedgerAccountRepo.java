package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.LedgerAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerAccountRepo extends JpaRepository<LedgerAccountEntity, Long> {
}
