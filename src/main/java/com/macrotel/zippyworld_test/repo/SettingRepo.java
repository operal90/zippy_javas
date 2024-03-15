package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SettingRepo extends JpaRepository<SettingEntity, Long> {
    @Query(value = "SELECT s FROM SettingEntity s WHERE s.name =:name")
    Optional<SettingEntity> getSettingByName(@Param("name") String name);
}
