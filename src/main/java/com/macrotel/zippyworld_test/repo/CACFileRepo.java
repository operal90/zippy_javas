package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.CACEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CACFileRepo extends JpaRepository<CACEntity, Long> {
    List<CACEntity> findByCustomerId(String customerId);
}
