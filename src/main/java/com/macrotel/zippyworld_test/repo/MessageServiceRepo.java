package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.MessageServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageServiceRepo extends JpaRepository<MessageServiceEntity, Long> {
}
