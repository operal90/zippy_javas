package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserSessionRepo extends JpaRepository<UserSessionEntity, Long> {
   Optional<UserSessionEntity> findByCustomerId(String customerId);
}
