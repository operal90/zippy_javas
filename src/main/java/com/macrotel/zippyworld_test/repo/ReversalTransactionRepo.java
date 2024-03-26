package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.ReversalTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReversalTransactionRepo extends JpaRepository<ReversalTransactionEntity, Long> {
}
