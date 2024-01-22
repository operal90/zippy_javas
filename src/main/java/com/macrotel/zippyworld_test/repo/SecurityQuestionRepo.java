package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.SecurityQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityQuestionRepo extends JpaRepository<SecurityQuestionEntity, Long> {
}
