package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.CustomerIdentityRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerIdentityRecordRepo extends JpaRepository<CustomerIdentityRecordEntity, Long> {
    Optional<CustomerIdentityRecordEntity> findByIdentityNumber(String identity);

    List<CustomerIdentityRecordEntity> findByCustomerId(String customerId);
}
