package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.IdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentityRepo extends JpaRepository<IdentityEntity, Long> {
    Optional<IdentityEntity> findByIdentityType (String identityType);

}
