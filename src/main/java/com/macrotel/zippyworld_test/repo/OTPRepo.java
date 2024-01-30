package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OTPRepo extends JpaRepository<OTPEntity, Long> {
    @Query(value = "SELECT o FROM OTPEntity o WHERE o.token1 =:userToken")
    Optional<OTPEntity> isTokenExist(@Param("userToken") String token);
}
