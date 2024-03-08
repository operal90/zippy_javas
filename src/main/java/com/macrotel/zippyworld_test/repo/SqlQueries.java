package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;

public interface SqlQueries extends JpaRepository<OTPEntity, Long> {
    @Query(value = "SELECT nos.service_account_no, nos.service_commission_account_no, nps.network, nps.code, nps.provider ,nos.description " +
            "FROM network_operator_services nos, network_provider_service_account_codes nps " +
            "WHERE nos.network_operator_code = :networkOperatorCode " +
            "AND nos.network_service_code = :networkServiceCode " +
            "AND nps.status = '0' " +
            "AND nos.id = nps.network_operator_service_id", nativeQuery = true)
    List<Object[]> networkOperatorServiceCode(@Param("networkServiceCode") String networkServiceCode, @Param("networkOperatorCode") String networkOperatorCode);
}
