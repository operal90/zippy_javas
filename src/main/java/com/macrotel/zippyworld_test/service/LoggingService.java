package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import com.macrotel.zippyworld_test.entity.*;
import com.macrotel.zippyworld_test.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class LoggingService {
    UtilityConfiguration utilityConfiguration = new UtilityConfiguration();
    @Autowired
    NetworkTxnLogRepo networkTxnLogRepo;
    @Autowired
    LedgerAccountRepo ledgerAccountRepo;
    @Autowired
    CustomerWalletRepo customerWalletRepo;
    @Autowired
    ServiceWalletRepo serviceWalletRepo;
    @Autowired
    ReversalTransactionRepo reversalTransactionRepo;
    @Autowired
    CustomerCommissionWalletRepo customerCommissionWalletRepo;
    @Autowired
    CommissionTxnLogRepo commissionTxnLogRepo;
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
        if(Objects.equals(operation,"AIRTIME-PURCHASE") || Objects.equals(operation,"DATA-PURCHASE")){
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

    public void serviceWalletLogging(String referenceId, String operationType, String userTypeId, String userPackageId, String serviceAccountNo, String customerId, String operationSummary,
                                     double amount, String commissionType, double commissionCharge, double amountCharge, double walletBalance, String operationAt){
        ServiceWalletEntity serviceWalletEntity = new ServiceWalletEntity();
        serviceWalletEntity.setReferenceId(referenceId);
        serviceWalletEntity.setOperationType(operationType);
        serviceWalletEntity.setUserTypeId(userTypeId);
        serviceWalletEntity.setUserPackageId(userPackageId);
        serviceWalletEntity.setServiceAccountNo(serviceAccountNo);
        serviceWalletEntity.setCustomerId(customerId);
        serviceWalletEntity.setOperationSummary(operationSummary);
        serviceWalletEntity.setAmount(utilityConfiguration.twoDecimalFormattedAmount(String.valueOf(amount)));
        serviceWalletEntity.setCommisionType(commissionType);
        serviceWalletEntity.setCommisionCharge(commissionCharge);
        serviceWalletEntity.setAmountCharge(utilityConfiguration.twoDecimalFormattedAmount(String.valueOf(amountCharge)));
        serviceWalletEntity.setWalletBalance(walletBalance);
        serviceWalletEntity.setOperationAt(operationAt);
        serviceWalletRepo.save(serviceWalletEntity);
    }

    public void reversalLogging(String reversalId, String operationId, String serviceAccountNo, String customerId, double amount){
        ReversalTransactionEntity reversalTransactionEntity = new ReversalTransactionEntity();
        reversalTransactionEntity.setReversalId(reversalId);
        reversalTransactionEntity.setOperationId(operationId);
        reversalTransactionEntity.setServiceAccountNo(serviceAccountNo);
        reversalTransactionEntity.setCustomerId(customerId);
        reversalTransactionEntity.setAmount(amount);
        reversalTransactionRepo.save(reversalTransactionEntity);
    }

    public void accumulateCommissionFundingLogging(String operationId, String customerId, String serviceAccountNumber, double commissionWalletBalance, double commissionAmount, String commissionOperationSummary){
        CustomerCommissionWalletEntity customerCommissionWalletEntity  = new CustomerCommissionWalletEntity();
        customerCommissionWalletEntity.setReferenceId(operationId);
        customerCommissionWalletEntity.setOperationType("CR");
        customerCommissionWalletEntity.setServiceAccountNo(serviceAccountNumber);
        customerCommissionWalletEntity.setOperationSummary(commissionOperationSummary);
        customerCommissionWalletEntity.setAmount(commissionAmount);
        customerCommissionWalletEntity.setCustomerId(customerId);
        customerCommissionWalletEntity.setWalletBalance(commissionWalletBalance);
        customerCommissionWalletRepo.save(customerCommissionWalletEntity);
    }
    public void instanceCommissionFundingLogging(String operationId, String customerId, String userTypeId, String userPackageId, String serviceAccountNumber, double amount,
                                                 double buyerWalletBalance, String operationSummary, String service){
        String txnId = operationId;
      operationId = operationId +"_CMS";
      CustomerWalletEntity customerWalletEntity = new CustomerWalletEntity();
      customerWalletEntity.setReferenceId(operationId);
      customerWalletEntity.setOperationEvent("COMM");
      customerWalletEntity.setOperationType("CR");
      customerWalletEntity.setUserTypeId(userTypeId);
      customerWalletEntity.setUserPackageId(userPackageId);
      customerWalletEntity.setServiceAccountNo(serviceAccountNumber);
      customerWalletEntity.setOperationSummary(operationSummary);
      customerWalletEntity.setAmount(amount);
      customerWalletEntity.setCommisionType("NN");
      customerWalletEntity.setCommisionMode("INSTANCE");
      customerWalletEntity.setCommisionCharge(0.0);
      customerWalletEntity.setAmountCharge(amount);
      customerWalletEntity.setCustomerId(customerId);
      customerWalletEntity.setWalletBalance(buyerWalletBalance);
      customerWalletEntity.setStatus("Successful");
      customerWalletRepo.save(customerWalletEntity);

      if(amount > 0){
            CommissionTxnLogEntity commissionTxnLogEntity = new CommissionTxnLogEntity();
            commissionTxnLogEntity.setOperationId(operationId);
            commissionTxnLogEntity.setTxnId(txnId);
            commissionTxnLogEntity.setServiceAmountNo(serviceAccountNumber);
            commissionTxnLogEntity.setService(service);
            commissionTxnLogEntity.setCommissionType("COMMISSION");
            commissionTxnLogEntity.setCustomerId(customerId);
            commissionTxnLogEntity.setAmount(amount);
            commissionTxnLogEntity.setDescription(operationSummary);
            commissionTxnLogEntity.setStatus("0");
            commissionTxnLogRepo.save(commissionTxnLogEntity);
      }

    }
}
