package com.macrotel.zippyworld_test.repo;

import com.macrotel.zippyworld_test.entity.NetworkTxnLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NetworkTxnLogRepo extends JpaRepository<NetworkTxnLogEntity, Long> {
    @Query(value = "SELECT * FROM network_txn_logs WHERE customer_id =:customerId AND recipient_no =:recipientNo" +
                    " ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<NetworkTxnLogEntity> customerRecipientLastTransaction(@Param("customerId") String customerId, @Param("recipientNo") String recipientNo);
}
