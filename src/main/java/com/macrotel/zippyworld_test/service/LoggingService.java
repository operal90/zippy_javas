package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.entity.NetworkTxnLogEntity;
import com.macrotel.zippyworld_test.repo.NetworkTxnLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class LoggingService {
    @Autowired
    NetworkTxnLogRepo networkTxnLogRepo;
    @Transactional
    public Long networkRequestLog(String operationId, String txnId, String channel, String userTypeId,
                                  String customerId, String userPackageId, float amount,
                                  double totalCommission, double totalCharge, String recipient,
                                  String serviceAccountNumber, String provider, String requestParam, String status, String rsCplxMsg, String rsActlMsg) {
        NetworkTxnLogEntity networkTxnLogEntity = new NetworkTxnLogEntity();
        networkTxnLogEntity.setOperationId(operationId);
        networkTxnLogEntity.setTxnId(txnId);
        networkTxnLogEntity.setChannel(channel);
        networkTxnLogEntity.setUserTypeId(userTypeId);
        networkTxnLogEntity.setCustomerId(customerId);
        networkTxnLogEntity.setUserPackageId(userPackageId);
        networkTxnLogEntity.setAmount(amount);
        networkTxnLogEntity.setCommissionCharge(String.valueOf(totalCommission));
        networkTxnLogEntity.setAmountCharge(String.valueOf(totalCharge));
        networkTxnLogEntity.setRecipientNo(recipient);
        networkTxnLogEntity.setServiceAccountNo(serviceAccountNumber);
        networkTxnLogEntity.setProvider(provider);
        networkTxnLogEntity.setRequestParam(requestParam);
        networkTxnLogEntity.setStatus(status);
        networkTxnLogEntity.setResponseComplexMessage(rsCplxMsg);
        networkTxnLogEntity.setResponseActualMessage(rsActlMsg);

        networkTxnLogEntity = networkTxnLogRepo.save(networkTxnLogEntity);
        return networkTxnLogEntity.getId();
    }

    public void responseTxnLogging(String operation, String id, String responseMessage, String status, String actualMessage){
        if(Objects.equals(operation,"airtime-purchase") || Objects.equals(operation,"data-purchase")){
            Optional<NetworkTxnLogEntity> getNetworkLog = networkTxnLogRepo.findById(Long.parseLong(id));
            NetworkTxnLogEntity networkTxnLog = getNetworkLog.get();
            networkTxnLog.setResponseComplexMessage(responseMessage);
            networkTxnLog.setResponseActualMessage(actualMessage);
            networkTxnLog.setStatus(status);
            networkTxnLog.setTimeOut(String.valueOf(LocalDateTime.now()));
            networkTxnLogRepo.save(networkTxnLog);
        }
    }

}
