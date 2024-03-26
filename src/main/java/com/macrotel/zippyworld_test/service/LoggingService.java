package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.entity.CustomerWalletEntity;
import com.macrotel.zippyworld_test.entity.LedgerAccountEntity;
import com.macrotel.zippyworld_test.entity.NetworkTxnLogEntity;
import com.macrotel.zippyworld_test.repo.CustomerWalletRepo;
import com.macrotel.zippyworld_test.repo.LedgerAccountRepo;
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
    @Autowired
    LedgerAccountRepo ledgerAccountRepo;
    @Autowired
    CustomerWalletRepo customerWalletRepo;
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

    public void ledgerAccountLogging(String referenceId, String operationType, String serviceAccountNo, String operationSummary,
                                     double amount, String customerId, String channel, String operationAt){
        LedgerAccountEntity ledgerAccountEntity = new LedgerAccountEntity();
        ledgerAccountEntity.setReferenceId(referenceId);
        ledgerAccountEntity.setOperationType(operationType);
        ledgerAccountEntity.setServiceAccountNo(serviceAccountNo);
        ledgerAccountEntity.setOperationSummary(operationSummary);
        ledgerAccountEntity.setAmount(String.valueOf(amount));
        ledgerAccountEntity.setCustomerId(customerId);
        ledgerAccountEntity.setChannel(channel);
        ledgerAccountEntity.setOperationAt(operationAt);
        ledgerAccountRepo.save(ledgerAccountEntity);
    }

    public void customerWalletLogging(String referenceId, String operationEvent, String operationType, String userTypeId, String userPackageId, String serviceAccountNo,
                                      String operationSummary, double amount, String commissionType, String commissionMode, double commissionCharge, double amountCharge,
                                      String customerId, double walletBalance, String operationAt){
        CustomerWalletEntity customerWalletEntity = new CustomerWalletEntity();
        customerWalletEntity.setReferenceId(referenceId);
        customerWalletEntity.setOperationEvent(operationEvent);
        customerWalletEntity.setOperationType(operationType);
        customerWalletEntity.setUserTypeId(userTypeId);
        customerWalletEntity.setUserPackageId(userPackageId);
        customerWalletEntity.setServiceAccountNo(serviceAccountNo);
        customerWalletEntity.setOperationSummary(operationSummary);
        customerWalletEntity.setAmount(amount);
        customerWalletEntity.setCommisionType(commissionType);
        customerWalletEntity.setCommisionMode(commissionMode);
        customerWalletEntity.setCommisionCharge(commissionCharge);
        customerWalletEntity.setAmountCharge(amountCharge);
        customerWalletEntity.setCustomerId(customerId);
        customerWalletEntity.setWalletBalance(walletBalance);
        customerWalletEntity.setOperationAt(operationAt);
        customerWalletRepo.save(customerWalletEntity);
    }
}