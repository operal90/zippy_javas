package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.MessageServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageServiceRepo extends JpaRepository<MessageServiceEntity, Long> {
    Optional<MessageServiceEntity> findByCustomerId(String customerId);
}
