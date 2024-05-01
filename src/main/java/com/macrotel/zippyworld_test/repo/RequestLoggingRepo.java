package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.RequestLoggingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLoggingRepo extends JpaRepository<RequestLoggingEntity, Long> {
}
