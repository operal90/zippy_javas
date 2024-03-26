package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.ServiceWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceWalletRepo extends JpaRepository<ServiceWalletEntity,Long> {
}
