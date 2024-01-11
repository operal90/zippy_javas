package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.CustomerWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerWalletRepo extends JpaRepository<CustomerWalletEntity, Long> {
}
