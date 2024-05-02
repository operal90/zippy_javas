package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.LoginTrackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LoginTrackerRepo extends JpaRepository<LoginTrackerEntity, Long> {
    List<LoginTrackerEntity> findByCustomerId(String customerId);

    @Transactional
    void deleteByCustomerId(String customerId);
}
