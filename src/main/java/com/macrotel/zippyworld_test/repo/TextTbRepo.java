package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.TextTbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextTbRepo extends JpaRepository<TextTbEntity, Long> {
}
