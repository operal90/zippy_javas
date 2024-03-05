package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.CustomerIdentityRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerIdentityRecordRepo extends JpaRepository<CustomerIdentityRecordEntity, Long> {
    Optional<CustomerIdentityRecordEntity> findByIdentityNumber(String identity);

    List<CustomerIdentityRecordEntity> findByCustomerId(String customerId);

    @Query(value = "SELECT c FROM CustomerIdentityRecordEntity c WHERE c.status='0' AND c.customerId =:customerId")
    List<CustomerIdentityRecordEntity> customerActiveKyc(@Param("customerId") String customerId);
}
