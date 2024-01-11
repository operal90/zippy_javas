package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.QRDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QRDetailRepo  extends JpaRepository<QRDetailsEntity, Long> {
}
