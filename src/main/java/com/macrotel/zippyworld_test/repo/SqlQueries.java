package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface SqlQueries extends JpaRepository<OTPEntity, Long> {
    @Query(value = "SELECT nos.service_account_no, nos.service_commission_account_no, nps.network, nps.code, nps.provider ,nos.description " +
            "FROM network_operator_services nos, network_provider_service_account_codes nps " +
            "WHERE nos.network_operator_code = :networkOperatorCode " +
            "AND nos.network_service_code = :networkServiceCode " +
            "AND nps.status = '0' " +
            "AND nos.id = nps.network_operator_service_id", nativeQuery = true)
    List<Object[]> networkOperatorServiceCode(@Param("networkServiceCode") String networkServiceCode, @Param("networkOperatorCode") String networkOperatorCode);

    @Query(value = "SELECT commision_type cmt , commision_percent cmp, commision_service_charge csc, master_value msv " +
            "FROM service_commision_charges WHERE " +
            "service_account_no =:serviceAccountNo AND user_type_id =:userTypeId  AND user_package_id =:userPackageId ", nativeQuery = true)
    List<Object[]> getCustomerCommissionDetail(@Param("serviceAccountNo") String serviceAccountNo, @Param("userTypeId") String userTypeId, @Param("userPackageId") String userPackageId);

    @Query(value = "SELECT commission_type ,zm , bm, bo FROM agent_commission_structures " +
            "WHERE user_id =:userId AND package_id =:packageId AND service_account_no =:serviceAccountNo", nativeQuery = true)
    List<Object[]> getAgentCommissionDetail(@Param("userId") String userId, @Param("packageId") String packageId, @Param("serviceAccountNo") String serviceAccountNo);

    @Query(value = "SELECT parent_aggregator_code FROM user_accounts WHERE phonenumber =:phoneNumber", nativeQuery = true)
    List<Object[]> getParentAggregatorCode(@Param("phoneNumber") String phoneNumber);

    @Query(value = "SELECT phonenumber FROM user_accounts WHERE aggregator_code =:code", nativeQuery = true)
    List<Object[]> getCustomerIdByCode(@Param("code") String code);

    @Query(value = "SELECT promo,code, pd.id, pd.type ,pd.user_type_id , pd.user_package_id , pd.value, pd.mode ,ps.commision_type cmt , ps.commision_percent cmp, " +
            "ps.commision_service_charge csc, ps.master_value msv FROM promo_details pd, promo_service_commision_charges ps where status = '0' and ps.promo_id = pd.id and " +
            "pd.service_account_no =:serviceAccountNo and date(NOW()) between start_dt and end_dt", nativeQuery = true)
    List<Object[]> getPromoCommissionProcess(@Param("serviceAccountNo") String serviceAccountNo);

    @Query(value = "SELECT DATEDIFF(NOW(), registered_at) date_diff from user_accounts where phonenumber =:phonenumber", nativeQuery = true)
    List<Object[]> getRegistrationDateDiff(@Param("phonenumber") String phonenumber);

    @Query(value = "SELECT session_close_at con FROM user_sessions where customer_id =:customerId AND token =:token order by id desc limit 1", nativeQuery = true)
    List<Object[]> getUserSession(@Param("customerId") String customerId, @Param("token") String token);
}
