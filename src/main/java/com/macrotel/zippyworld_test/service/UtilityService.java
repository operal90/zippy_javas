package com.macrotel.zippyworld_test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.macrotel.zippyworld_test.config.Notification;
import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import com.macrotel.zippyworld_test.dto.MessageServiceDTO;
import com.macrotel.zippyworld_test.entity.*;
import com.macrotel.zippyworld_test.pojo.UtilityResponse;
import com.macrotel.zippyworld_test.provider.HESProvider;
import com.macrotel.zippyworld_test.provider.ShagoConnect;
import com.macrotel.zippyworld_test.provider.MacrotelConnect;
import com.macrotel.zippyworld_test.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.macrotel.zippyworld_test.config.AppConstants.*;
@Service
public class UtilityService {
    private final LoggingService loggingService;
    private final NotificationService notificationService;
    public UtilityService(LoggingService loggingService, @Lazy NotificationService notificationService){
        this.loggingService = loggingService;
        this.notificationService = notificationService;
    }
    UtilityResponse utilityResponse = new UtilityResponse();
    UtilityConfiguration utilityConfiguration = new UtilityConfiguration();
    HESProvider hesProvider = new HESProvider();
    MacrotelConnect macrotelConnect = new MacrotelConnect();
    ShagoConnect shagoConnect = new ShagoConnect();
    Notification notification = new Notification();
    @Autowired
    SqlQueries sqlQueries;
    @Autowired
    SettingRepo settingRepo;
    @Autowired
    UserAccountRepo userAccountRepo;
    @Autowired
    MessageServiceRepo messageServiceRepo;
    @Autowired
    UserSessionRepo userSessionRepo;
    @Autowired
    LoginTrackerRepo loginTrackerRepo;
    @Autowired
    DTOService dtoService;
    @Autowired
    AutoPrivatePowerLogRepo autoPrivatePowerLogRepo;
    @Autowired
    PosPaymentNotificationRepo posPaymentNotificationRepo;
    @Autowired
    FundTransferTxnLogRepo fundTransferTxnLogRepo;
    @Autowired
    ProvidusSettlementNotificationRepo providusSettlementNotificationRepo;
    @PersistenceContext
    private EntityManager entityManager;


    public UtilityResponse agentCommissionStructure(double amount, String buzCharacter, String customerId, String userId, String packageId, String serviceAccountNo){
        //Get AgentCommissionStructure
        HashMap<String,Object> result = new HashMap<>();
        List<Object[]> getAgentCommission = sqlQueries.getAgentCommissionDetail(userId,packageId,serviceAccountNo);
        if(getAgentCommission.isEmpty()){
            utilityResponse.setStatusCode(ERROR_STATUS_CODE);
            utilityResponse.setMessage("No record found");
            utilityResponse.setResult(EMPTY_RESULT);
            return utilityResponse;
        }
        Object[] agentCommission = getAgentCommission.get(0);
        String commissionType = agentCommission[0].toString();
        double zonalManager = (agentCommission[1] !=null &&!agentCommission[1].toString().isEmpty()) ? Double.parseDouble(agentCommission[1].toString()) : 0.0;
        double businessManager = (agentCommission[2] !=null &&!agentCommission[2].toString().isEmpty()) ? Double.parseDouble(agentCommission[2].toString()) : 0.0;
        double businessOwner = (agentCommission[3] !=null &&!agentCommission[3].toString().isEmpty()) ? Double.parseDouble(agentCommission[3].toString()) : 0.0;

        //Get Parent Aggregator Code
        String parentAggregatorCode = this.getParentAggregatorCodeOne(customerId);

        //Get UserSettingValue
        Map<String, Object> getUserSettings = (Map<String, Object>) this.getSettingValue("DEFAULT_AGGREGATOR_CODE");
        String userSettingsValue = (String) getUserSettings.get("result");
        if(!parentAggregatorCode.equals(userSettingsValue)){
             if(Objects.equals(buzCharacter,"BM")){
                 String zmCustomerId = this.getCustomerIdByCode(parentAggregatorCode);
                 double bmCommissionPercent  = businessOwner + businessManager;
                 double bmCommission = utilityConfiguration.formattedAmount(String.valueOf(bmCommissionPercent/100 * amount));
                 double zmCommission = utilityConfiguration.formattedAmount(String.valueOf(zonalManager /100 * amount));

                 Map<String, String> bo = new HashMap<>();
                 bo.put("agentType", "BO");
                 bo.put("customerId", "0");
                 bo.put("commission", "0.0");
                 bo.put("process", "NO");

                 Map<String, Object> bm = new HashMap<>();
                 bm.put("agentType", "BM");
                 bm.put("customerId", customerId);
                 bm.put("commission", String.valueOf(bmCommission));
                 bm.put("process", "NO");

                 Map<String, Object> zm = new HashMap<>();
                 zm.put("agentType", "ZM");
                 zm.put("customerId", zmCustomerId);
                 zm.put("commission", String.valueOf(zmCommission));
                 zm.put("process", "YES");

                 result.put("bo",bo);
                 result.put("bm", bm);
                 result.put("zm", zm);

             }
             else if (Objects.equals(buzCharacter, "BO")){
                 String bmCustomerId =this.getCustomerIdByCode(this.getParentAggregatorCodeOne(customerId));
                 String zmCustomerId = this.getCustomerIdByCode(this.getParentAggregatorCodeOne(bmCustomerId));

                 double boCommission = utilityConfiguration.formattedAmount(String.valueOf(businessOwner/100 * amount));
                 double bmCommission = utilityConfiguration.formattedAmount(String.valueOf(businessManager/100 *amount));
                 double zmCommission = utilityConfiguration.formattedAmount(String.valueOf(zonalManager /100 *amount));
                 Map<String, String> bo = new HashMap<>();
                 bo.put("agentType", "BO");
                 bo.put("customerId", customerId);
                 bo.put("commission", String.valueOf(boCommission));
                 bo.put("process", "NO");

                 Map<String, Object> bm = new HashMap<>();
                 bm.put("agentType", "BM");
                 bm.put("customerId", bmCustomerId);
                 bm.put("commission", String.valueOf(bmCommission));
                 bm.put("process", "YES");

                 Map<String, Object> zm = new HashMap<>();
                 zm.put("agentType", "ZM");
                 zm.put("customerId", zmCustomerId);
                 zm.put("commission", String.valueOf(zmCommission));
                 zm.put("process", "YES");

                 result.put("bo",bo);
                 result.put("bm", bm);
                 result.put("zm", zm);
             }
        }

        utilityResponse.setStatusCode(SUCCESS_STATUS_CODE);
        utilityResponse.setResult(result);
        return utilityResponse;
    }


    public Object getPromoServiceCommission(String userTypeId, String userPackageId, String customerId, String serviceAccountNo, double amount, String parentAggregatorCode){
        HashMap<String, Double> result = new HashMap<>();
        double commissionUser = 0.0;
        double commissionMaster = 0.0;
        //GetPromoCommissionProcess
        Object getPromoCommission = this.getPromoCommissionProcess(userTypeId,userPackageId,customerId,serviceAccountNo,amount);
        if(getPromoCommission instanceof HashMap){
            HashMap<String, String>promoResult = (HashMap<String, String>) getPromoCommission;
            String statusCode = promoResult.get("statusCode");
            String cmt = promoResult.get("cmt");
            double cmp  = Double.parseDouble(promoResult.get("cmp"));
            double csc  = Double.parseDouble(promoResult.get("csc"));
            double msv = Double.parseDouble(promoResult.get("msv"));
            if(!statusCode.equals("1")){
                if(cmt.equals("PT")){
                    commissionUser = (amount * cmp) /1000;
                    commissionMaster = (msv > 0) ? (amount * msv)/100 : msv;
                    Map<String, Object> getUserSettings = (Map<String, Object>) this.getSettingValue("DEFAULT_AGGREGATOR_CODE");
                    String userSettingsValue = (String) getUserSettings.get("result");
                    if(Objects.equals(userSettingsValue,parentAggregatorCode)){
                        commissionUser+=commissionMaster;
                        commissionMaster = 0.0;
                    }
                }
                else{
                    commissionUser = csc;
                    commissionMaster = msv;
                }
            }
        }
        result.put("commissionUser", commissionUser);
        result.put("commissionMaster", commissionMaster);
        return result;
    }

    public Object getPromoCommissionProcess(String userTypeId, String userPackageId, String customerId, String serviceAccountNo, double amount){
        HashMap<String, String> result = new HashMap<>();
        result.put("statusCode", "1");
        result.put("cmt", "0");
        result.put("cmp", "0");
        result.put("csc", "0");
        result.put("msv", "0");

        List<Object[]> getPromoCommissionQuery = sqlQueries.getPromoCommissionProcess(serviceAccountNo);
        if (!getPromoCommissionQuery.isEmpty()) {
            Object[] promoCommissionQuery = getPromoCommissionQuery.get(0);
            String mode = promoCommissionQuery[7].toString();
            String type = promoCommissionQuery[3].toString();
            String typeId = promoCommissionQuery[4].toString();
            String packageId = promoCommissionQuery[5].toString();
            double value = Double.parseDouble(promoCommissionQuery[6].toString());

            if(Objects.equals(mode, "SPECIFIC")){
                if(Objects.equals(type,"DATE_RANGE")){
                    if(!Objects.equals(typeId,"") && !Objects.equals(packageId,"")){
                        if(Objects.equals(typeId,userTypeId) && Objects.equals(packageId,userPackageId)){
                            setResultValues(result, promoCommissionQuery);
                        }
                    }
                } else if (Objects.equals(type,"CUSTOMER_AGE")) {
                    double customerDateDiff = this.getRegistrationDateDiff(customerId);
                    if(customerDateDiff <= value){
                        setResultValues(result, promoCommissionQuery);
                    }
                }
            } else if (Objects.equals(mode,"NON_SPECIFIC")) {
                if(Objects.equals(type,"DATE_RANGE")){
                    setResultValues(result, promoCommissionQuery);
                }
                else if (Objects.equals(type,"CUSTOMER_AGE")) {
                    double customerDateDiff = this.getRegistrationDateDiff(customerId);
                    if(customerDateDiff <= value){
                        setResultValues(result, promoCommissionQuery);
                    }
                }
            }

        }
        return result;
    }

    public Object getServiceCommission2(double amount, String serviceAccountNumber,String userTypeId, String userPackageId){
        HashMap<String, Double> result = new HashMap<>();
        double commissionUser = 0.0;
        double commissionMaster = 0.0;

        List<Object[]> getCustomerCommission = sqlQueries.getCustomerCommissionDetail(serviceAccountNumber,userTypeId,userPackageId);
        if(!getCustomerCommission.isEmpty()){
            Object[] customerCommission = getCustomerCommission.get(0);
            String cmt = (customerCommission.length > 0 && customerCommission[0] != null) ? customerCommission[0].toString() : "";
            double cmp = (customerCommission[1] != null && !customerCommission[1].toString().isEmpty()) ? Double.parseDouble(customerCommission[1].toString()) : 0.0;
            double csc = (customerCommission[2] != null && !customerCommission[2].toString().isEmpty()) ? Double.parseDouble(customerCommission[2].toString()) : 0.0;
            double msv = (customerCommission[3] != null && !customerCommission[3].toString().isEmpty()) ? Double.parseDouble(customerCommission[3].toString()) : 0.0;

            if(Objects.equals(cmt,"PT")){
                commissionUser = (amount * cmp) /100;
                commissionMaster = (msv > 0) ? (amount * msv)/100 : msv;
            }
            else {
                commissionUser = csc;
                commissionMaster = msv;
            }
        }
        result.put("commissionUser", commissionUser);
        result.put("commissionMaster", commissionMaster);
        return result;
    }

    private void setResultValues(Map<String, String> result, Object[] promoCommissionQuery) {
        result.put("statusCode", "0");
        result.put("cmt", promoCommissionQuery[8].toString());
        result.put("cmp", promoCommissionQuery[9].toString());
        result.put("csc", promoCommissionQuery[10].toString());
        result.put("msv", promoCommissionQuery[11].toString());
    }
    private String getParentAggregatorCodeOne(String customerId){
        List<Object[]> getParentAggregatorCode = sqlQueries.getParentAggregatorCode(customerId);
        Object[] parentAggregatorCode = getParentAggregatorCode.get(0);
        return parentAggregatorCode[0].toString();
    }

    private String getCustomerIdByCode(String code){
        List<Object[]> getCustomerId = sqlQueries.getCustomerIdByCode(code);
        Object[] customerId = getCustomerId.get(0);
        return  customerId[0].toString();
    }
    public Object getSettingValue(String name){
        //Get Settings
        Optional<SettingEntity> getSettings = settingRepo.getSettingByName(name);
        Map<String, Object> result = new HashMap<>();
        if(getSettings.isPresent()){
            SettingEntity settingEntity = getSettings.get();
            result.put("statusCode", "0");
            result.put("result", settingEntity.getValue());
        }
        else{
            result.put("statusCode", "1");
            result.put("result", "");
        }
        return result;
    }

    private Double getRegistrationDateDiff(String phoneNumber){
        List<Object[]> getCustomerDateDiff = sqlQueries.getRegistrationDateDiff(phoneNumber);
        Object[] customerDateDiff = getCustomerDateDiff.get(0);
        return  Double.parseDouble(customerDateDiff[0].toString());
    }

    public Integer checkAggregatorFund(String phoneNumber){
        int response = 0;
        double dateDiff = this.getRegistrationDateDiff(phoneNumber);
        Map<String, Object> getUserSettings = (Map<String, Object>) this.getSettingValue("AGGREGATOR_COMMISSION_RECEIVER_NO_OF_DAYS");
        double settingValue = Double.parseDouble((String) getUserSettings.get("result"));
        if(dateDiff > settingValue){
            response =1;
        }
        return response;
    }
    public Integer checkSessionToken(String customerId, String token){
       int response = 1;
        List<Object[]> getCustomerSession = sqlQueries.getUserSession(customerId,token);
        if(!getCustomerSession.isEmpty()){
            Object[] customerSession = getCustomerSession.get(0);
            LocalDateTime sessionTime = LocalDateTime.parse(customerSession[0].toString(), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.S"));
            LocalDateTime currentTime = LocalDateTime.now();

            long years = ChronoUnit.YEARS.between(sessionTime,currentTime);
            long months = ChronoUnit.MONTHS.between(sessionTime,currentTime);
            long days = ChronoUnit.DAYS.between(sessionTime,currentTime);
            long hours = ChronoUnit.HOURS.between(sessionTime,currentTime);
            long minutes = ChronoUnit.MINUTES.between(sessionTime,currentTime);

            if(years > 0 || months >0 || days >0 || hours > 0){
                response = 1;
            }else{
                if(minutes <= 20){
                    response = 0;
                }
            }
        }
       return response;
    }

    public Object airtimePurchase(String operationId, String customerId, String customerName, String email, String userTypeId, String userPackageId, String commissionMode,
                                String airtimeBeneficiary, double amount, double commissionAmount, double amountCharge, String channel, String serviceAccountNumber,
                                  String serviceCommissionAccountNumber, String networkOperatorCode, String networkServiceCode, String provider, String network,
                                  String operationCode, String operationSummary){
        HashMap<String, Object> result = new HashMap<>();
        String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
        double formattedAmount = utilityConfiguration.formattedAmount(String.valueOf(amount));

        //Check Daily Transaction Balance
        Object checkDailyTxnBalance = this.checkDailyTxnBalance(customerId,amountCharge,serviceAccountNumber);
        Map<String, String> dailyTxnBalance = (Map<String, String>) checkDailyTxnBalance;
        String dailyTxnBalanceStatusCode = dailyTxnBalance.get("statusCode");
        String dailyTxnBalanceMessage = dailyTxnBalance.get("message");
        if(Objects.equals(dailyTxnBalanceStatusCode, "0")){
            //Get customer wallet balance
            Object getCustomerWalletBalance = this.queryCustomerWalletBalance(customerId);
            Map<String, String> customerWalletBalance = (Map<String, String>) getCustomerWalletBalance;
            String customerWalletBalanceStatusCode = customerWalletBalance.get("statusCode");
            double customerWalletBalanceAmount = Double.parseDouble(customerWalletBalance.get("amount"));
            //Get Service Wallet Balance
            double walletBalance = this.queryServiceWalletBalance(serviceAccountNumber);

            if(Objects.equals(customerWalletBalanceStatusCode, "0")){
                if(customerWalletBalanceAmount >= amountCharge){
                      operationSummary = customerName + " recharges " +airtimeBeneficiary+" with "+ network+" N"+formattedAmount;
                     String commissionOperationSummary = "Commission on recharges for "+customerName+" ,"+airtimeBeneficiary +" "+ network+" of N"+formattedAmount;
                     double buyerWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(customerWalletBalanceAmount - amount));
                     double receiverWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(walletBalance+amountCharge));

                     //Log Into Ledger Account(CR and DR),service wallet, customer wallet.
                    loggingService.ledgerAccountLogging(operationId,"CR",serviceAccountNumber,operationSummary,amount,customerId,channel,todayDate);
                    loggingService.ledgerAccountLogging(operationId,"DR",serviceAccountNumber,operationSummary,amount,customerId,channel,todayDate);
                    loggingService.serviceWalletLogging(operationId,"CR",userTypeId,userPackageId,serviceAccountNumber,customerId,operationSummary,amount,
                                        "PT",commissionAmount,amountCharge,receiverWalletBalance,todayDate);
                    loggingService.customerWalletLogging(operationId,"MAIN","DR",userTypeId,userPackageId,serviceAccountNumber,operationSummary,
                                                        amount,"PT",commissionMode,0,amount,customerId,buyerWalletBalance,"",todayDate);

                    //Consult third-party telecom
                    List<Object> airtimeVendingAPI = (List<Object>) macrotelConnect.airtimeVendingRequest(network, airtimeBeneficiary, formattedAmount, operationId);
                    Object airtimeResponse = airtimeVendingAPI.get(0);
                    Map<String, Object> airtimeResponseMap = (Map<String, Object>) airtimeResponse;
                    String statusCode = (String) airtimeResponseMap.get("statusCode");
                    Object details = airtimeResponseMap.get("details");
                    Map<String, String> detailsMap = (Map<String, String>)details;

                    if(!Objects.equals(statusCode, "0")){
                        //Reversal
                        String reversalId = utilityConfiguration.getOperationId("NU");
                        operationSummary = "Reversal of, " + operationSummary;
                        loggingService.reversalLogging(reversalId,operationId,serviceAccountNumber,customerId,formattedAmount);
                        this.reversalOperation(reversalId,customerId,userTypeId,userPackageId,amount,channel,serviceAccountNumber,operationSummary);
                        String message ="Dear "+customerName+", your recharge of N"+formattedAmount+" failed and it has been auto reversed. Kindly retry. Thank you for using Zippyworld";
                        result.put("statusCode", "0");
                        result.put("message", message);
                        result.put("statusMessage", "Pending");
                        result.put("amount", String.valueOf(formattedAmount));
                        result.put("reference", operationId);
                        result.put("recipient", airtimeBeneficiary);
                        result.put("description", "Issue from "+provider);
                        result.put("recipientName", "NIL");
                        result.put("network", detailsMap.get("network"));
                        result.put("operationSummary", operationSummary);
                        result.put("referenceNumber", detailsMap.get("reference_number"));
                    }
                    else {
                        if (commissionAmount > 0) {
                            if (Objects.equals(commissionMode, "ACCUMULATE")) {
                                Object getCustomerCommissionWalletBalance = this.queryCustomerCommissionWalletBalance(customerId);
                                Map<String, String> customerCommissionWalletBalance = (Map<String, String>) getCustomerCommissionWalletBalance;
                                double commissionWalletBalance = Double.parseDouble(customerWalletBalance.get("amount")) + commissionAmount;
                                loggingService.accumulateCommissionFundingLogging(operationId, customerId, serviceCommissionAccountNumber, commissionWalletBalance, commissionAmount, commissionOperationSummary);
                            } else {
                                buyerWalletBalance = buyerWalletBalance + commissionAmount;
                                loggingService.instanceCommissionFundingLogging(operationId, customerId, userTypeId, userPackageId, serviceCommissionAccountNumber, commissionAmount, buyerWalletBalance, commissionOperationSummary, "Network Airtime Vending");
                            }
                        }

                        String message = "Dear " + customerName + ", your recharge of N" +amount+" for "+ airtimeBeneficiary + " was successful. Thank you for using Zippyworld. REF:" + operationId;
                        result.put("statusCode", "0");
                        result.put("message", message);
                        result.put("statusMessage", "Successful");
                        result.put("amount", String.valueOf(formattedAmount));
                        result.put("reference", operationId);
                        result.put("recipient", airtimeBeneficiary);
                        result.put("recipientName", "NIL");
                        result.put("network", detailsMap.get("network"));
                        result.put("operationSummary", operationSummary);
                        result.put("referenceNumber", detailsMap.get("reference_number"));
                        //Send Notification to user
                       notificationService.sendAirtimeNotification(customerId,userTypeId,userPackageId,customerName,email,amount);
                    }
                }
                else{
                    result.put("statusCode", "1");
                    result.put("message", "Insufficient Wallet Balance");
                    result.put("statusMessage", "Failed");
                    result.put("description", "");
                }
            }
            else {
                result.put("statusCode", "3");
                result.put("message", customerWalletBalance.get("message"));
                result.put("statusMessage", "Failed");
                result.put("description", "");
            }
        }
        else{
            result.put("statusCode", "1");
            result.put("message", dailyTxnBalanceMessage);
            result.put("statusMessage", "Failed");
        }
        return result;
    }

    public Object checkDailyTxnBalance(String customerId, double txnAmount, String serviceAccountNumber){
        HashMap<String, String> response = new HashMap<>();
        List<Object[]> getCustomerKycAmount = sqlQueries.getKycAmount(customerId);
        Object[] customerKycAmount = getCustomerKycAmount.get(0);
        String customerName = (String) customerKycAmount[0];
        String restriction = (String) customerKycAmount[3];
        int kycLevel = (customerKycAmount[2] !=null && !customerKycAmount[2].toString().isEmpty()) ? Integer.parseInt(customerKycAmount[2].toString()) : 0;
        double kycAmount = (customerKycAmount[1] !=null && !customerKycAmount[1].toString().isEmpty()) ? Double.parseDouble(customerKycAmount[1].toString()) : 0.0;

        Object dailyTxnBalanceProcess = this.dailyTxnBalanceProcess(customerName,kycLevel,kycAmount,customerId,txnAmount);
        Map<String, String> txnBalanceProcess = (Map<String, String>) dailyTxnBalanceProcess;
        if(!Objects.equals(restriction, "YES")){
            response.put("statusCode", txnBalanceProcess.get("statusCode"));
            response.put("message", txnBalanceProcess.get("message"));
        }
        else{
            Object getKycAllowService = this.getKycAllowService(kycLevel,serviceAccountNumber);
            Map<String, String> kycAllowService = (Map<String, String>) getKycAllowService;
            String kycAllowServiceStatusCode = kycAllowService.get("statusCode");
            if(!Objects.equals(kycAllowServiceStatusCode,"1")){
                response.put("statusCode", txnBalanceProcess.get("statusCode"));
                response.put("message", txnBalanceProcess.get("message"));
            }
            else{
                response.put("statusCode", "1");
                response.put("message", "Dear "+customerName+", Kindly provide your MEANS OF IDENTITY to enjoy the transaction. Thanks for using Zippyworld");
            }
        }
        return response;
    }
    public Object dailyTxnBalanceProcess(String kycCustomerName, int kycLevel, double kycAmount, String customerId, double txnAmount){
        //Get User Daily Amount Spend
        List<Object[]> getCustomerDailyAmount = sqlQueries.getCustomerDailyAmount(customerId);
        Object[] customerDailyAmount = getCustomerDailyAmount.get(0);
        double dailyAmountSpend =  (customerDailyAmount[0] !=null && !customerDailyAmount[0].toString().isEmpty()) ? Double.parseDouble(customerDailyAmount[0].toString()) : 0.0;

        //Get Customer Daily reversalAmount
        List<Object[]> getCustomerDailyReversalAmount = sqlQueries.getCustomerDailyReversalAmount(customerId);
        Object[] customerDailyReversalAmount = getCustomerDailyReversalAmount.get(0);
        double dailyReversalAmount =  (customerDailyReversalAmount[0] !=null && !customerDailyReversalAmount[0].toString().isEmpty()) ? Double.parseDouble(customerDailyReversalAmount[0].toString()) : 0.0;

        HashMap<String, String> response = new HashMap<>();
        if(kycLevel > 0){
            double dailyAmount = utilityConfiguration.formattedAmount(String.valueOf(kycAmount));
            double balance = kycAmount - (dailyAmountSpend - dailyReversalAmount);
            if(txnAmount <= balance){
                balance =  utilityConfiguration.formattedAmount(String.valueOf(balance));
                response.put("statusCode", "0");
                response.put("message", "You have N"+balance+" Available to use for today");
            }
            else{
                balance = utilityConfiguration.formattedAmount(String.valueOf(balance));
                if(kycLevel < 4){
                    response.put("statusCode", "1");
                    response.put("message", "Dear "+ kycCustomerName+", your spending limit for the day is N"+dailyAmount+". Kindly upgrade your KYC to the next level to adjust your daily limit, Thanks for using Zippyworld");
                }
                else{
                    response.put("statusCode", "1");
                    response.put("message", "Dear "+ kycCustomerName+", your spending limit for the day is N"+dailyAmount+". Thanks for using Zippyworld");
                }
            }
        }
        else{
            response.put("statusCode", "1");
            response.put("message", "Dear "+kycCustomerName+", Kindly update your BVN to enjoy the transaction. Thanks for using Zippyworld");
        }
        return response;
    }

    public Object getKycAllowService(int kycLevel, String serviceAccountNumber){
        HashMap<String, String> response = new HashMap<>();
        List<Object[]> getKycAllowService = sqlQueries.getKycAllowService(String.valueOf(kycLevel), serviceAccountNumber);
        if(!getKycAllowService.isEmpty()){
            Object[] kycAllowService = getKycAllowService.get(0);
            if (kycAllowService.length > 0 && kycAllowService[0] != null) {
                String id = kycAllowService[0].toString();
                response.put("statusCode", "0");
                response.put("result", id);
            } else {
                response.put("statusCode", "1");
                response.put("result", "ID exists but has no associated value");
            }
        }
        else{
            response.put("statusCode", "1");
            response.put("result", "No record found");
        }
        return response;
    }

    public Object queryCustomerWalletBalance(String customerId){
        HashMap<String, String> response = new HashMap<>();
        String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
        response.put("statusCode", "1");
        response.put("amount", "0");
        response.put("message", "Unable to Get wallet balance");

        //Get customer wallet balance
        List<Object[]> getCustomerWalletBalance = sqlQueries.getCustomerWalletBalance(customerId);
        if(!getCustomerWalletBalance.isEmpty()){
            Object[] customerWalletBalance = getCustomerWalletBalance.get(0);
            double walletBalance = Double.parseDouble(customerWalletBalance[0].toString());
            String operationAt = customerWalletBalance[1].toString();

            if(!Objects.equals(todayDate, operationAt)){
                response.put("statusCode", "0");
                response.put("amount", String.valueOf(walletBalance));
                response.put("message", "Successful");
            }
            else{
                response.put("statusCode", "2");
                response.put("amount", String.valueOf(walletBalance));
                response.put("message", "Concurrent Operation");
            }
        }
        return response;
    }
    public Object queryQrCustomerWalletBalance(String customerId){
        HashMap<String, String> result = new HashMap<>();
        String currentDate= LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        result.put("status_code","1");
        result.put("message","Unable to get Wallet Balance");
        result.put("amount", "");
        //Query qr wallet balance
        List<Object[]> getCustomerQrBalance = sqlQueries.getCustomerQrBalance(customerId);
        if(!getCustomerQrBalance.isEmpty()){
            Object[] customerQrWalletBalance = getCustomerQrBalance.get(0);
            String walletBalance = customerQrWalletBalance[0].toString();
            String operationAt = customerQrWalletBalance[1].toString();

            if(!Objects.equals(currentDate,operationAt)){
                result.put("status_code", "0");
                result.put("amount", walletBalance);
                result.put("message", "Successful");
            }
            else{
                result.put("status_code", "2");
                result.put("amount", walletBalance);
                result.put("message", "Concurrent Operation");
            }
        }
        return  result;
    }

    public Object queryCustomerCommissionWalletBalance(String customerId){
        HashMap<String, String> response = new HashMap<>();
        String todayDate = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));
        response.put("statusCode", "1");
        response.put("amount", "0");
        response.put("message", "Unable to Get wallet balance");

        //Get customer wallet balance
        List<Object[]> getCustomerCommissionWalletBalance = sqlQueries.getCustomerCommissionWalletBalance(customerId);
        if(!getCustomerCommissionWalletBalance.isEmpty()){
            Object[] customerCommissionWalletBalance = getCustomerCommissionWalletBalance.get(0);
            double walletBalance = (customerCommissionWalletBalance[0] !=null &&!customerCommissionWalletBalance[0].toString().isEmpty()) ? Double.parseDouble(customerCommissionWalletBalance[0].toString()) : 0;
            String operationAt =  (customerCommissionWalletBalance[1] == null) ? "" : customerCommissionWalletBalance[1].toString() ;
            if(!Objects.equals(todayDate, operationAt)){
                response.put("statusCode", "0");
                response.put("amount", String.valueOf(walletBalance));
                response.put("message", "Successful");
            }
            else{
                response.put("statusCode", "2");
                response.put("amount", String.valueOf(walletBalance));
                response.put("message", "Concurrent Operation");
            }

        }
        return response;
    }

    public Double queryServiceWalletBalance(String serviceAccountNo){
        double amount = 0.0;
        List<Object[]> getServiceWalletBalance = sqlQueries.getServiceWalletBalance(serviceAccountNo);
        if(!getServiceWalletBalance.isEmpty()) {
            Object[] serviceWalletBalance = getServiceWalletBalance.get(0);
            amount = Double.parseDouble(serviceWalletBalance[0].toString());
        }
        return amount;
    }

    public Object queryCustomerCommissionEarned(String customerId){
        HashMap<String, String> result = new HashMap<>();
        String amount = "0";
        List<Object[]> getCustomerCommissionEarned = sqlQueries.getCustomerCommissionEarned(customerId);
        if(!getCustomerCommissionEarned.isEmpty()){
            Object[] customerCommission = getCustomerCommissionEarned.get(0);
            amount= (customerCommission[0]== null ? "0" : customerCommission[0].toString());
        }
        result.put("status_code", "0");
        result.put("message","Successful");
        result.put("amount", amount);
        return result;
    }

    public void reversalOperation(String operationId, String customerId, String userTypeId, String userPackageId, double amount, String channel, String serviceAccountNumber, String operationSummary){
        HashMap<String, String> result = new HashMap<>();
        String todayDate = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));

        Object getCustomerWalletBalance = this.queryCustomerWalletBalance(customerId);
        Map<String, String> customerWalletBalance = (Map<String, String>) getCustomerWalletBalance;
        String customerWalletBalanceStatusCode = customerWalletBalance.get("statusCode");
        double customerWalletBalanceAmount = Double.parseDouble(customerWalletBalance.get("amount"));
        String customerWalletBalanceMessage = customerWalletBalance.get("message");
        if(Objects.equals(customerWalletBalanceStatusCode,"0")){
            //Get customer serviceWalletBalance
            double serviceWalletBalance = this.queryServiceWalletBalance(serviceAccountNumber);
            double newServiceWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(serviceWalletBalance -amount));
            double newBalance = utilityConfiguration.formattedAmount(String.valueOf(customerWalletBalanceAmount + amount));

            //Log Into Ledger Account(CR and DR),service wallet, customer wallet.
            loggingService.ledgerAccountLogging(operationId,"CR",serviceAccountNumber,operationSummary,amount,customerId,channel,todayDate);
            loggingService.ledgerAccountLogging(operationId,"DR",serviceAccountNumber,operationSummary,amount,customerId,channel,todayDate);
            loggingService.serviceWalletLogging(operationId,"DR",userTypeId,userPackageId,serviceAccountNumber,customerId,operationSummary,amount,
                    "NN",0,amount,newServiceWalletBalance,todayDate);
            loggingService.customerWalletLogging(operationId,"RVSL","CR",userTypeId,userPackageId,serviceAccountNumber,operationSummary,
                    amount,"NN","",0,amount,customerId,newBalance,"",todayDate);

            result.put("statusCode", "0");
            result.put("reference", operationId);
            result.put("message", "Reversal operation successful");
        }
        else{
            result.put("statusCode", customerWalletBalanceStatusCode);
            result.put("reference", "");
            result.put("message", customerWalletBalanceMessage);
        }
    }

    public void aggregatorFunding(String operationId, String creditorId, String aggregatorCode, double amount, String serviceAccountNumber, String operationSummary){
    }
    public Boolean confirmSecurityAnswer(String customerId, String answer){
        boolean response = false;
        Optional<UserAccountEntity> confirmSecurityAnswer = userAccountRepo.confirmSecurityAnswer(customerId,answer);
        if(confirmSecurityAnswer.isPresent()){
            response = true;
        }
        return response;
    }


    public Integer checkPreviousTxnStatus(String customerId, String identityNumber, String serviceCode){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int response = 1;
        List <Object[]>result = new ArrayList<>();
        String currentTime = String.valueOf(LocalDateTime.now().plusHours(1).format(formatter));
        String currentTimePlusFive = String.valueOf(LocalDateTime.now().plusHours(1).plusMinutes(59).format(formatter));

        if(Objects.equals(serviceCode,TELCOM_SERVICE_CODE)){
            result = sqlQueries.networkTxnLogPreviousTime(customerId,identityNumber,currentTime,currentTimePlusFive);
        } else if (Objects.equals(serviceCode, ELECTRICITY_SERVICE_CODE)) {
            result = sqlQueries.electricityTxnLogPreviousTime(customerId,identityNumber,currentTime,currentTimePlusFive);
        } else if (Objects.equals(serviceCode, CABLE_TV_SERVICE_CODE)) {
            result = sqlQueries.cableTvTxnLogPreviousTime(customerId,identityNumber,currentTime,currentTimePlusFive);
        } else if (Objects.equals(serviceCode, BANK_TRANSFER_SERVICE_CODE)) {
            result = sqlQueries.bankTrfTxnLogPreviousTime(customerId,identityNumber,currentTime,currentTimePlusFive);
        }
        if(result !=null && !result.isEmpty() )
        {
            response = 0;
        }
        return response;
    }

    public Object electricityPurchase(String operationId, String customerId, String customerName, String email, String userTypeId, String userPackageId, String commissionMode,
                                      String cardIdentity, String serviceAccountNumber, String serviceCommissionAccountNumber, double amount, double commissionAmount,
                                      double amountCharge, String channel, String accountTypeId, String operatorId, String providerRef, String buyerPhoneNumber,
                                      String buyerEmail, String provider, String buyerName, String customerAddress, String value){
        HashMap<String, Object> result = new HashMap<>();
        String token = "";
        String providerResponse = "";
        String todayDate = String.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        //Check Daily Transaction Balance
        Object checkDailyTxnBalance = this.checkDailyTxnBalance(customerId,amountCharge,serviceAccountNumber);
        Map<String, String> dailyTxnBalance = (Map<String, String>) checkDailyTxnBalance;
        String dailyTxnBalanceStatusCode = dailyTxnBalance.get("statusCode");
        String dailyTxnBalanceMessage = dailyTxnBalance.get("message");
        if(Objects.equals(dailyTxnBalanceStatusCode, "0")){
            //Get customer wallet balance
            Object getCustomerWalletBalance = this.queryCustomerWalletBalance(customerId);
            Map<String, String> customerWalletBalance = (Map<String, String>) getCustomerWalletBalance;
            String customerWalletBalanceStatusCode = customerWalletBalance.get("statusCode");
            double customerWalletBalanceAmount = Double.parseDouble(customerWalletBalance.get("amount"));

            if(Objects.equals(customerWalletBalanceStatusCode, "0")){
                if(customerWalletBalanceAmount >= amountCharge){
                    //Get Service Account Name details
                    String serviceName = this.getServiceAccountDetails(serviceAccountNumber);
                    if(!Objects.equals(serviceName, "")){

                        double formattedAmount = utilityConfiguration.twoDecimalFormattedAmount(String.valueOf(amount));
                        String operationSummary = customerName + " sold electricity power of N"+formattedAmount+" to  "+buyerName+" ("+cardIdentity+")";
                        String commissionOperationSummary = "Commission on sold of electricity power of N"+formattedAmount+" to  "+buyerName+" ("+cardIdentity+")";
                        double buyerWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(customerWalletBalanceAmount - amount));
                        //Get Service Wallet Balance
                        double walletBalance = this.queryServiceWalletBalance(serviceAccountNumber);
                        double receiverWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(walletBalance +amountCharge));

//                        Log Into Ledger Account(CR and DR),service wallet, customer wallet.
                        loggingService.ledgerAccountLogging(operationId,"CR",serviceAccountNumber,operationSummary,amountCharge,customerId,channel,todayDate);
                        loggingService.ledgerAccountLogging(operationId,"DR",serviceAccountNumber,operationSummary,amountCharge,customerId,channel,todayDate);
                        loggingService.serviceWalletLogging(operationId,"CR",userTypeId,userPackageId,serviceAccountNumber,customerId,operationSummary,amount,
                                "",commissionAmount,amountCharge,receiverWalletBalance,todayDate);
                        loggingService.customerWalletLogging(operationId,"MAIN","DR",userTypeId,userPackageId,serviceAccountNumber,operationSummary,
                                amount,"PT",commissionMode,0,amount,customerId,buyerWalletBalance,"",todayDate);
                        amount = utilityConfiguration.zeroDecimalFormattedAmount(String.valueOf(amount));
                        String serviceCode =  this.getElectricityOperatorCode(operatorId,accountTypeId);
                        String discoCode = this.getDiscoCode(operatorId);
                        String discoName = this.getDiscoName(operationId);
                        Object thirdPartyElectricityResponse = new Object[0];
                        if(Objects.equals(provider,"SHAGO")){
                            thirdPartyElectricityResponse = shagoConnect.electricityConnect(operationId,cardIdentity,amount,accountTypeId,operatorId,discoCode,buyerPhoneNumber,buyerName,customerAddress);
                        }

                        Map<String, Object> electricityResponseMap = (Map<String, Object>) thirdPartyElectricityResponse;
                        String electricityApiStatusCode = (String) electricityResponseMap.get("statusCode");
                        String electricityApiToken = (String) electricityResponseMap.get("token");
                        String electricityApiMessage = (String) electricityResponseMap.get("message");
                        String electricityApiStatusMessage  = (String) electricityResponseMap.get("statusMessage");
                        Object electricityApiData = electricityResponseMap.get("data");
                        Object electricityApiResponse = electricityResponseMap.get("apiResponse");

                        if(!Objects.equals(electricityApiStatusCode, "0")){
                            if(Objects.equals(electricityApiStatusCode, "1")){
                                //Reversal
                                String reversalId = utilityConfiguration.randomDigit(10);
                                operationSummary = "Reversal of "+operationSummary;
                                loggingService.reversalLogging(reversalId,operationId,serviceAccountNumber,customerId,amount);
                                this.reversalOperation(reversalId,customerId,userTypeId,userPackageId,amount,channel,serviceAccountNumber,operationSummary);
                                String reversalMessage = "Dear "+customerName +", "+electricityApiMessage+" .Thank you for using Zippyworld";
                                result.put("statusCode", "1");
                                result.put("message", reversalMessage);
                                result.put("messageDetails" , electricityResponseMap);
                                result.put("statusMessage", electricityApiStatusMessage);
                                result.put("token", "");
                            }
                            else{
                                String reversalMessage = "Dear "+customerName +", "+electricityApiMessage+" .Thank you for using Zippyworld";
                                result.put("statusCode", "1");
                                result.put("message", reversalMessage);
                                result.put("statusMessage", electricityApiStatusMessage);
                                result.put("messageDetails" , electricityResponseMap);
                                result.put("token", "");
                            }
                        }
                        else{
                            if (commissionAmount > 0) {
                                if (Objects.equals(commissionMode, "ACCUMULATE")) {
                                    Object getCustomerCommissionWalletBalance = this.queryCustomerCommissionWalletBalance(customerId);
                                    Map<String, String> customerCommissionWalletBalance = (Map<String, String>) getCustomerCommissionWalletBalance;
                                    double commissionWalletBalance = Double.parseDouble(customerWalletBalance.get("amount")) + commissionAmount;
                                    loggingService.accumulateCommissionFundingLogging(operationId, customerId, serviceCommissionAccountNumber, commissionWalletBalance, commissionAmount, commissionOperationSummary);
                                } else {
                                    buyerWalletBalance = buyerWalletBalance + commissionAmount;
                                    loggingService.instanceCommissionFundingLogging(operationId, customerId, userTypeId, userPackageId, serviceCommissionAccountNumber, commissionAmount, buyerWalletBalance, commissionOperationSummary, "Electricity Service");
                                }
                            }
                            if(Objects.equals(accountTypeId, "1")){
                                //PREPAID
                                token = electricityApiToken;
                                loggingService.insertIntoTextTb(operationId, token);
                                String msg = "Dear "+customerName+", your transaction is successful and the power token is "+token+". Thanks for using Zippyworld";
                                result.put("statusCode", "0");
                                result.put("reference", operationId);
                                result.put("message", msg);
                                result.put("token", token);
                                result.put("messageDetails", electricityResponseMap);
                                result.put("statusMessage", "Successful");
                                result.put("amount", amount);
                                result.put("recipient", cardIdentity);
                                result.put("recipientName", buyerName);
                            }
                            else{
                                //POSTPAID
                                String msg = "Dear "+customerName+", your transaction is successful. Thanks for using Zippyworld";
                                result.put("statusCode", "0");
                                result.put("reference", operationId);
                                result.put("message", "Transaction Successful");
                                result.put("token", "");
                                result.put("messageDetails", electricityResponseMap);
                                result.put("statusMessage", "Successful");
                                result.put("amount", amount);
                                result.put("recipient", cardIdentity);
                                result.put("recipientName", buyerName);
                            }
                        }
                        //Call notification method
                        notificationService.sendElectricityNotification(customerId,userTypeId,userPackageId,customerName,email,amount,cardIdentity,buyerName,token,discoName);
                    }
                    else{
                        result.put("statusCode", "1");
                        result.put("message", "Invalid Service Code");
                        result.put("statusMessage", "Failed");
                        result.put("token", "");
                    }
                }
                else{
                    result.put("statusCode", "1");
                    result.put("message", "Insufficient Wallet Balance");
                    result.put("statusMessage", "Failed");
                    result.put("token", "");
                }
            }
            else{
                result.put("statusCode", "1");
                result.put("message", customerWalletBalance.get("message"));
                result.put("statusMessage", "Failed");
                result.put("token", "");
            }
        }
        else{
            result.put("statusCode", "1");
            result.put("message", dailyTxnBalanceMessage);
            result.put("token", "");
            result.put("statusMessage", "Failed");
        }
        return result;
    }


    public String getServiceAccountDetails(String serviceAccountCode){
        String serviceName = "";
        List<Object[]> getServiceAccountDetails = sqlQueries.getServiceAccountName(serviceAccountCode);
        if(!getServiceAccountDetails.isEmpty()){
            Object[] serviceAccountDetails = getServiceAccountDetails.get(0);
            serviceName = serviceAccountDetails[0].toString();
        }
        return serviceName;
    }

    public String getElectricityOperatorCode(String operatorId, String accountTypeId){
        String code ="";
        List<Object[]> getElectricityOperatorCode = sqlQueries.getElectricityOperatorCode(operatorId, accountTypeId);
        if(!getElectricityOperatorCode.isEmpty()){
            Object[] electricityOperatorCode = getElectricityOperatorCode.get(0);
            code = electricityOperatorCode[0].toString();
        }
        return code;
    }

    public String getDiscoCode(String id){
        String code ="";
        List<Object[]> getElectricityDiscoCode = sqlQueries.getElectricityDiscoCode(id);
        if(!getElectricityDiscoCode.isEmpty()){
            Object[] electricityOperatorCode = getElectricityDiscoCode.get(0);
            code = electricityOperatorCode[0].toString();
        }
        return code;
    }
    public String getDiscoName(String id){
        String discoName ="";
        List<Object[]> getElectricityDiscoCode = sqlQueries.getElectricityDiscoCode(id);
        if(!getElectricityDiscoCode.isEmpty()){
            Object[] electricityOperatorCode = getElectricityDiscoCode.get(0);
            discoName = electricityOperatorCode[1].toString();
        }
        return discoName;
    }

    public Boolean isPhoneNumberUnique(String phoneNumber){
        boolean response = false;
        Optional<UserAccountEntity> isUserAccountExist = userAccountRepo.getUserAccountDetails(phoneNumber);
        if(isUserAccountExist.isPresent()){
            response = true;
        }
        return response;
    }

    public void isSessionExist (String customerId){
        //Get User session
        Optional<UserSessionEntity> getUserSession = userSessionRepo.findByCustomerId(customerId);
        if(getUserSession.isPresent()){
            UserSessionEntity userSessionEntity = getUserSession.get();
            //Delete the user session
            userSessionRepo.delete(userSessionEntity);
        }
    }

    public Integer loginCounter(String customerId){
        int count = 0;
        List<Object[]> getUserLoginCount = sqlQueries.countLoginTracker(customerId);
        if(!getUserLoginCount.isEmpty()){
           Object[] userLoginCount = getUserLoginCount.get(0);
            count = Integer.parseInt(userLoginCount[0].toString());
        }
        return count;
    }

    public void loginCounterDelete(String customerId){
        loginTrackerRepo.deleteByCustomerId(customerId);
    }

    public Integer loginTracker(String customerId){
        int counter = 0;
        //Insert into LoginTracker Db
        LoginTrackerEntity loginTrackerEntity = new LoginTrackerEntity();
        loginTrackerEntity.setCustomerId(customerId);
        loginTrackerEntity.setTryCount(1);
        loginTrackerRepo.save(loginTrackerEntity);

        //Get user LoginCounter;
        counter = this.loginCounter(customerId);
        if(counter >= 5){
            //Update user account and set it status to 1
            Optional<UserAccountEntity> getUserAccountDetails = userAccountRepo.findByPhonenumber(customerId);
            if(getUserAccountDetails.isPresent()){
                UserAccountEntity userAccountEntity = getUserAccountDetails.get();
                userAccountEntity.setStatus("1");
                userAccountRepo.save(userAccountEntity);
            }
        }
        return counter;
    }

    public String userAccountStatus (String customerId){
        String userStatus = "";
        Optional<UserAccountEntity> getUserData = userAccountRepo.findByPhonenumber(customerId);
        if(getUserData.isPresent()){
            UserAccountEntity userAccountEntity = getUserData.get();
            userStatus = userAccountEntity.getStatus();
        }
        return userStatus;
    }

    public String generateSessionToken(String customerId, String pin){
        String token="";
        String randomNumber =  utilityConfiguration.randomAlphanumeric(15);
        token = utilityConfiguration.shaEncryption(customerId+pin+randomNumber);
        //Save Session Token
        UserSessionEntity userSessionEntity = new UserSessionEntity();
        userSessionEntity.setCustomerId(customerId);
        userSessionEntity.setToken(token);
        userSessionRepo.save(userSessionEntity);
        return token;
    }

    public String isSoftPosUsedSuccessful(String customerId){
        String response = "1";
        List<Object[]> getCustomerSettlementCount = sqlQueries.countSoftPosSuccessful(customerId);
        if(!getCustomerSettlementCount.isEmpty()){
            Object[] userCountResult = getCustomerSettlementCount.get(0);
            int count = Integer.parseInt(userCountResult[0].toString());
            if(count > 0){
                response = "0";
            }
        }
        return response;
    }

    public String isTaxCollector(String customerId){
        String response = "1";
        List<Object[]> getUserTaxCollectorCount = sqlQueries.isTaxCollector(customerId);
        if(!getUserTaxCollectorCount.isEmpty()){
            Object[] userCountResult = getUserTaxCollectorCount.get(0);
            int count = Integer.parseInt(userCountResult[0].toString());
            if(count > 0){
                response = "0";
            }
        }
        return response;
    }

    public String isPosKeyPadUser(String customerId){
        String response = "1";
        List<Object[]> getUserKeyPadPosCount = sqlQueries.isPosKeyPadUser(customerId);
        if(!getUserKeyPadPosCount.isEmpty()){
            Object[] userCountResult = getUserKeyPadPosCount.get(0);
            int count = Integer.parseInt(userCountResult[0].toString());
            if(count > 0){
                response = "0";
            }
        }
        return response;
    }
    public Object customerLogin(String customerId, String pin){
        HashMap<String, Object> result = new HashMap<>();
        pin = utilityConfiguration.shaEncryption(pin);
        int counter = this.loginTracker(customerId);

        Optional<UserAccountEntity> authenticateUser = userAccountRepo.authenticateUser(customerId,pin);
        if(authenticateUser.isEmpty()){
            if(counter > 2){
                int times = 5 - counter;
                if(times ==0){
                    result.put("statusCode", "1");
                    result.put("message", "Your Account is locked, Kindly contact customer care on 09020195199 or Email at Zippyworld@macrotelgroup.com ");
                }
                else{
                    result.put("statusCode", "1");
                    result.put("message", "Your account will be locked after "+times+" more trial");
                }
            }
            else{
                result.put("statusCode", "1");
                result.put("message","Phone number or Password is not correct");
            }
        }
        else{
            UserAccountEntity userAccountEntity = authenticateUser.get();
            if(counter > 0){
                this.loginCounterDelete(customerId);
            }
            //Get User POS Data if user is a POS Agent, Get if user is a Tax collector, Get if user is a KeypadPOS user, Get the messageService User Subscribe for
            //Get customer QRWallet Balance, commisionWalletBalance , wallet balance and commission earned
            MessageServiceDTO messageServiceDTO = new MessageServiceDTO();
            Optional<MessageServiceEntity> getUserMessageSubscribe = messageServiceRepo.findByCustomerId(customerId);
            if(getUserMessageSubscribe.isPresent()){
                MessageServiceEntity messageServiceEntity = getUserMessageSubscribe.get();
                messageServiceDTO = dtoService.messageServiceDTO(messageServiceEntity);
            }
            //Generate session token
            String sessionToken = this.generateSessionToken(customerId,pin);
            //De structure the response gotten
            HashMap<String, String> qrCustomerWalletBalanceResponse = (HashMap<String, String>) queryQrCustomerWalletBalance(customerId);
            HashMap<String, String>  queryCustomerCommissionWalletBalance = (HashMap<String, String>) queryCustomerCommissionWalletBalance(customerId);
            HashMap<String, String> customerWalletBalanceResponse = (HashMap<String, String>) queryCustomerWalletBalance(customerId);
            HashMap<String, String>  commissionEarnedResponse = (HashMap<String, String>) queryCustomerCommissionEarned(customerId);
            result.put("statusCode","0");
            result.put("message", "Login Successfully");
            result.put("token", sessionToken);
            result.put("user_detail", dtoService.userAccountDTO(userAccountEntity));
            result.put("softpos_used_status", this.isSoftPosUsedSuccessful(customerId));
            result.put("is_tax_collector", this.isTaxCollector(customerId));
            result.put("is_pos_keypad_user", this.isPosKeyPadUser(customerId));
            result.put("message_detail", messageServiceDTO);
            result.put("qr_customer_wallet_balance", qrCustomerWalletBalanceResponse.get("amount"));
            result.put("customer_commission_wallet_balance", queryCustomerCommissionWalletBalance.get("amount"));
            result.put("customer_wallet_balance", customerWalletBalanceResponse.get("amount"));
            result.put("commission_earned", commissionEarnedResponse.get("amount"));
        }
        return result;
    }
    public Object getNetworkOperatorServiceCode(String networkOperatorCode, String networkServiceCode){
        HashMap<String, String> result = new HashMap<>();
        List<Object[]> getNetworkOperatorService = sqlQueries.networkOperatorServiceCode(networkServiceCode,networkOperatorCode);
        if(!getNetworkOperatorService.isEmpty()){
            Object[] networkOperatorServiceCode = getNetworkOperatorService.get(0);
            result.put("serviceAccountNumber" , networkOperatorServiceCode[0].toString());
            result.put("serviceCommissionAccountNumber" , networkOperatorServiceCode[1].toString());
            result.put("network" , networkOperatorServiceCode[2].toString());
            result.put("operationCode" , networkOperatorServiceCode[3].toString());
            result.put("provider" , networkOperatorServiceCode[4].toString());
            result.put("description" , networkOperatorServiceCode[5].toString());
        }
        return result;
    }

    public Object dataPlanList(String networkServiceCode, String network){
        HashMap<String, Object> result = new HashMap<>();
        if(network.equals("MTN") || network.equals("AIRTEL") || network.equals("GLO") || network.equals("9MOBILE")){
            
        } else if (network.equals("AIRTELL") || network.equals("GLOO") || network.equals("9MOBILEE")) {

        }
        return result;
    }

    public Object dataPurchase(String operationId, String customerId, String customerName, String email, String userTypeId, String userPackageId, String commissionMode,
                               String dataBeneficiary, String serviceAccountNumber, String serviceCommissionAccountNumber, double amount, String planCode, double commissionAmount,
                               double amountCharge, String channel, String operatorServiceCode, String operationSummary, String provider, String network){
        HashMap<String, Object> result = new HashMap<>();
        String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
        double formattedAmount = utilityConfiguration.formattedAmount(String.valueOf(amount));

        //Check Daily Transaction Balance
        Object checkDailyTxnBalance = this.checkDailyTxnBalance(customerId,amountCharge,serviceAccountNumber);
        Map<String, String> dailyTxnBalance = (Map<String, String>) checkDailyTxnBalance;
        String dailyTxnBalanceStatusCode = dailyTxnBalance.get("statusCode");
        String dailyTxnBalanceMessage = dailyTxnBalance.get("message");
        if(Objects.equals(dailyTxnBalanceStatusCode, "0")){
            //Get customer wallet balance
            Object getCustomerWalletBalance = this.queryCustomerWalletBalance(customerId);
            Map<String, String> customerWalletBalance = (Map<String, String>) getCustomerWalletBalance;
            String customerWalletBalanceStatusCode = customerWalletBalance.get("statusCode");
            double customerWalletBalanceAmount = Double.parseDouble(customerWalletBalance.get("amount"));
            //Get Service Wallet Balance
            double walletBalance = this.queryServiceWalletBalance(serviceAccountNumber);
            if(Objects.equals(customerWalletBalanceStatusCode, "0")){
                if(customerWalletBalanceAmount >= amountCharge) {
                    operationSummary = customerName + " recharges " + dataBeneficiary + " with " + network + " data bundle of N" + formattedAmount;
                    String commissionOperationSummary = "Commission on recharges for " + customerName + " ," + dataBeneficiary + " " + network + " data bundle of N" + formattedAmount;
                    double buyerWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(customerWalletBalanceAmount - amount));
                    double receiverWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(walletBalance + amountCharge));

                    //Log Into Ledger Account(CR and DR),service wallet, customer wallet.
                    loggingService.ledgerAccountLogging(operationId, "CR", serviceAccountNumber, operationSummary, amount, customerId, channel, todayDate);
                    loggingService.ledgerAccountLogging(operationId, "DR", serviceAccountNumber, operationSummary, amount, customerId, channel, todayDate);
                    loggingService.serviceWalletLogging(operationId, "CR", userTypeId, userPackageId, serviceAccountNumber, customerId, operationSummary, amount,
                            "PT", commissionAmount, amountCharge, receiverWalletBalance, todayDate);
                    loggingService.customerWalletLogging(operationId, "MAIN", "DR", userTypeId, userPackageId, serviceAccountNumber, operationSummary,
                            amount, "PT", commissionMode, 0, amount, customerId, buyerWalletBalance, "", todayDate);

                    double dataAmount = utilityConfiguration.zeroDecimalFormattedAmount(String.valueOf(formattedAmount));
                    double userMessageAmount = utilityConfiguration.twoDecimalFormattedAmount(String.valueOf(amount));

                    //Consume third party API
                    List<Object> dataVendingAPI = (List<Object>) macrotelConnect.dataVendingRequest(network, dataBeneficiary, dataAmount, planCode, operationId);
                    Object dataResponse = dataVendingAPI.get(0);
                    Map<String, Object> dataResponseMap = (Map<String, Object>) dataResponse;
                    String statusCode = (String) dataResponseMap.get("statusCode");
                    Object details = dataResponseMap.get("details");
                    Map<String, String> detailsMap = (Map<String, String>)details;
                    if(!Objects.equals(statusCode, "0")){
                        //Reversal
                        String reversalId = utilityConfiguration.getOperationId("NU");
                        operationSummary = "Reversal of " + operationSummary;
                        loggingService.reversalLogging(reversalId,operationId,serviceAccountNumber,customerId,formattedAmount);
                        this.reversalOperation(reversalId,customerId,userTypeId,userPackageId,amount,channel,serviceAccountNumber,operationSummary);
                        String message ="Dear "+customerName+", your data bundle  of N"+formattedAmount+" failed and it has been auto reversed. Kindly retry. Thank you for using Zippyworld";
                        result.put("statusCode", "0");
                        result.put("message", message);
                        result.put("statusMessage", "Pending");
                        result.put("description", "Issue from "+provider);
                        result.put("amount", userMessageAmount);
                        result.put("reference", operationId);
                        result.put("recipient", dataBeneficiary);
                        result.put("recipientName", "NIL");
                        result.put("network", detailsMap.get("network"));
                        result.put("operationSummary", operationSummary);
                        result.put("referenceNumber", detailsMap.get("reference_number"));
                    }
                    else {
                        if (commissionAmount > 0) {
                            if (Objects.equals(commissionMode, "ACCUMULATE")) {
                                Object getCustomerCommissionWalletBalance = this.queryCustomerCommissionWalletBalance(customerId);
                                Map<String, String> customerCommissionWalletBalance = (Map<String, String>) getCustomerCommissionWalletBalance;
                                double commissionWalletBalance = Double.parseDouble(customerWalletBalance.get("amount")) + commissionAmount;
                                loggingService.accumulateCommissionFundingLogging(operationId, customerId, serviceCommissionAccountNumber, commissionWalletBalance, commissionAmount, commissionOperationSummary);
                            } else {
                                buyerWalletBalance = buyerWalletBalance + commissionAmount;
                                loggingService.instanceCommissionFundingLogging(operationId, customerId, userTypeId, userPackageId, serviceCommissionAccountNumber, commissionAmount, buyerWalletBalance, commissionOperationSummary, "Network Airtime Vending");
                            }
                        }

                        String message = "Dear " + customerName + ", your data bundle of N" +userMessageAmount+" for "+ dataBeneficiary + " was successful. Thank you for using Zippyworld. REF:" + operationId;
                        result.put("statusCode", "0");
                        result.put("message", message);
                        result.put("statusMessage", "Successful");
                        result.put("amount", userMessageAmount);
                        result.put("reference", operationId);
                        result.put("recipient", dataBeneficiary);
                        result.put("recipientName", "NIL");
                        result.put("network", detailsMap.get("network"));
                        result.put("operationSummary", operationSummary);
                        result.put("referenceNumber", detailsMap.get("reference_number"));
                        //Send Notification to user
                        notificationService.sendDataNotification(customerId,userTypeId,userPackageId,customerName,email,amount);
                    }
                }
                else{
                    result.put("statusCode", "1");
                    result.put("message", "Insufficient Wallet Balance");
                    result.put("statusMessage", "Failed");
                    result.put("description", "");
                }

            }
            else {
                result.put("statusCode", "1");
                result.put("message", customerWalletBalance.get("message"));
                result.put("statusMessage", "Failed");
                result.put("description", "");
            }
        }
        else{
            result.put("statusCode", "1");
            result.put("message", dailyTxnBalanceMessage);
            result.put("statusMessage", "Failed");
        }
        return result;
    }

    public Object getEstateCode(String cardIdentity){
        HashMap<String, Object> result = new HashMap<>();

        List<Object[]> getEstateCode = sqlQueries.getCustomerEstateCode(cardIdentity);
        if(!getEstateCode.isEmpty()){
            Object[] estateCode = getEstateCode.get(0);
            result.put("statusCode", "0");
            result.put("message", "Record found");
            result.put("estateCode", estateCode[0].toString());
        }
        else{
            result.put("statusCode", "1");
            result.put("message", "No record found");
            result.put("estateCode", "");
        }
        return result;
    }

    public Object getServiceCommissionDetails(String serviceAccountNo, String userType){
        HashMap<String, String> result = new HashMap<>();
        List<Object[]> getCommissionDetails = sqlQueries.getServiceCommissionDetails(serviceAccountNo, userType);
        if(!getCommissionDetails.isEmpty()){
            Object[] commissionDetails = getCommissionDetails.get(0);
            result.put("cmt", commissionDetails[0] !=null && !commissionDetails[0].toString().isEmpty() ? commissionDetails[0].toString() : "");
            result.put("cmp", commissionDetails[1] !=null && !commissionDetails[1].toString().isEmpty() ? commissionDetails[1].toString() : "0");
            result.put("csc", commissionDetails[2] !=null && !commissionDetails[2].toString().isEmpty() ? commissionDetails[2].toString() : "0");
        }
        else{
            result.put("cmt", "");
            result.put("cmp", "0");
            result.put("csc", "0");
        }
        return result;
    }

    public Double getServiceCommission(double amount, String serviceAccountNo, String userType){
        double commissionAmount = 0;
        Object getServiceCommissionDetails = this.getServiceCommissionDetails(serviceAccountNo,userType);
        Map<String, String> serviceCommissionDetails = (Map<String, String>) getServiceCommissionDetails;
        String serviceCommissionType = serviceCommissionDetails.get("cmt");
        double serviceCommissionAmount = Double.parseDouble(serviceCommissionDetails.get("cmp"));
        if(serviceCommissionType.equals("PT")){
            commissionAmount = (amount * serviceCommissionAmount) /100;
        }
        else{
            commissionAmount = serviceCommissionAmount;
        }
        return commissionAmount;
    }

    public Object getClientAccountUserDetails(String clientId){
        HashMap<String, String> result = new HashMap<>();
        List<Object[]> getClientAccountDetails = sqlQueries.getClientAccountUserDetail(clientId);
        if(!getClientAccountDetails.isEmpty()) {
            Object[] clientAccountDetails = getClientAccountDetails.get(0);
            result.put("statusCode", "0");
            result.put("accountNumber", clientAccountDetails[0].toString());
            result.put("accountName", clientAccountDetails[1].toString());
            result.put("bankCode", clientAccountDetails[2].toString());
        }
        else{
            result.put("statusCode", "1");
        }
        return result;
    }

    public Object autoPrivatePowerVending(String operationId, String customerId, String userTypeId, String cardIdentity, String serviceAccountNumber, double amount,
                                          double commissionAmount, double amountCharge, String channel, String loadingType, String estateCode, String userPackageId) throws JsonProcessingException {

        HashMap<String, Object> result = new HashMap<>();
        String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
        List<Map<String, Object>> getUserDetails = sqlQueries.getUserDetail(customerId);
        Map<String, Object> customerDetails = getUserDetails.get(0);
        String customerName = String.valueOf(customerDetails.get("names"));
        String bankTransferStatus = "";
        String operationSummary = "";
        String operationSummary1 = "";

        Object checkDailyTxnBalance = this.checkDailyTxnBalance(customerId,amountCharge,serviceAccountNumber);
        Map<String, String> dailyTxnBalance = (Map<String, String>) checkDailyTxnBalance;
        String dailyTxnBalanceStatusCode = dailyTxnBalance.get("statusCode");
        String dailyTxnBalanceMessage = dailyTxnBalance.get("message");
        if(dailyTxnBalanceStatusCode.equals("0")){
            //Get customer wallet balance
            Object getCustomerWalletBalance = this.queryCustomerWalletBalance(customerId);
            Map<String, String> customerWalletBalance = (Map<String, String>) getCustomerWalletBalance;
            String customerWalletBalanceStatusCode = customerWalletBalance.get("statusCode");
            double customerWalletBalanceAmount = Double.parseDouble(customerWalletBalance.get("amount"));

            if(Objects.equals(customerWalletBalanceStatusCode, "0")){
                if(customerWalletBalanceAmount>=amountCharge){
                    //Get Service Wallet Balance
                    double serviceWalletBalance = this.queryServiceWalletBalance(serviceAccountNumber);

                    //Query commission collector wallet balance
                    Object getCommissionWalletBalance = this.queryCustomerWalletBalance(PRIVATE_ESTATE_COMMISSION_COLLECTOR);
                    Map<String, String> commissionCollectorWalletBalanceMap = (Map<String, String>) getCommissionWalletBalance;
                    double commissionCollectorWalletBalanceAmount = Double.parseDouble(commissionCollectorWalletBalanceMap.get("amount"));
                    String serviceName = this.getServiceAccountDetails(serviceAccountNumber);

                    if(!Objects.equals(serviceName, "")){
                        if(estateCode.equals("ZWPPP1774")){
                            operationSummary= "TA Garden Auto Power Recharhage Commission";
                            operationSummary1 = customerName+", of TA Garden bought "+serviceName+" electricity of "+amountCharge;
                        } else if (estateCode.equals("ZWPPP1777")) {
                            operationSummary= "Goosepen Auto Power Recharhage Commission";
                            operationSummary1 = customerName+", of Goosepen bought "+serviceName+" electricity of "+amountCharge;
                        } else if (estateCode.equals("ZWPPP1778")) {
                            operationSummary= "Glover Auto Power Recharhage Commission";
                            operationSummary1 = customerName+", of Glover bought "+serviceName+" electricity of "+amountCharge;
                        }
                        double buyerWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(customerWalletBalanceAmount - amountCharge));
                        double commissionCollectorWalletBalance = utilityConfiguration.formattedAmount(String.valueOf(commissionCollectorWalletBalanceAmount + PRIVATE_ESTATE_COMMISSION_AMOUNT));
                        double receiverWalletBalance = utilityConfiguration.formattedAmount(String.valueOf((serviceWalletBalance + (amountCharge -PRIVATE_ESTATE_COMMISSION_AMOUNT))));

                        //Logging
                        loggingService.ledgerAccountLogging(operationId,"CR",serviceAccountNumber,operationSummary,amountCharge,customerId,channel,todayDate);
                        loggingService.ledgerAccountLogging(operationId,"DR",serviceAccountNumber,operationSummary,amountCharge,customerId,channel,todayDate);
                        loggingService.serviceWalletLogging(operationId,"CR",userTypeId,userPackageId,serviceAccountNumber,customerId,operationSummary,amount,"",commissionAmount,amountCharge,
                                                            receiverWalletBalance,todayDate);
                        loggingService.customerWalletLogging(operationId,"MAIN","DR",userTypeId,userPackageId,serviceAccountNumber,operationSummary1,amount,"","",
                                                            commissionAmount,amountCharge,customerId,buyerWalletBalance,"",todayDate);
                        loggingService.customerWalletLogging(operationId+"_CMS", "MAIN","CR",userTypeId,userPackageId,serviceAccountNumber,operationSummary,PRIVATE_ESTATE_COMMISSION_AMOUNT,
                                                "NN","",0,PRIVATE_ESTATE_COMMISSION_AMOUNT,PRIVATE_ESTATE_COMMISSION_COLLECTOR,commissionCollectorWalletBalance,"2",todayDate);


                        //Consume third party API to buy token
                        Object buyMeterToken = hesProvider.buyToken(cardIdentity,customerName,amount, Integer.valueOf(loadingType));
                        Map<String, Object> meterTokenResponseMap = (Map<String, Object>) buyMeterToken;
                        String meterTokenStatusCode = (String) meterTokenResponseMap.get("statusCode");
                        String meterToken = (String) meterTokenResponseMap.get("token");
                        String meterTokenMessage = (String) meterTokenResponseMap.get("message");
                        String meterTokenResponse = (String) meterTokenResponseMap.get("response");
                        Object meterTokenResult = meterTokenResponseMap.get("result");

                        if(!meterTokenStatusCode.equals("1")){
                            double transferAmount = amountCharge -100;
                            //Fix the bank transfer status
                            Object getClientAccountDetails = this.getClientAccountUserDetails(estateCode);
                            Map<String, String>clientAccountDetail  = (Map<String, String>)getClientAccountDetails;
                            String clientAccountStatusCode = clientAccountDetail.get("statusCode");
                            if(clientAccountStatusCode.equals("0")){
                                String clientAccountNumber = clientAccountDetail.get("accountNumber");
                                String clientAccountBankCode = clientAccountDetail.get("bankCode");
                                String clientAccountName = clientAccountDetail.get("accountName");
                                Object bankTransferEp = macrotelConnect.bankTransferEp(operationId,clientAccountNumber,clientAccountBankCode,transferAmount,clientAccountName,customerId,cardIdentity+" Macrotel Innovations Ltd", operationSummary1);
                                Map<String, Object> bankTransferResponseMap = (Map<String, Object>) bankTransferEp;
                                String transferStatusCode = (String) bankTransferResponseMap.get("statusCode");
                                Object transferDetails = bankTransferResponseMap.get("details");
                                loggingService.insertIntoTextTb(operationId, new ObjectMapper().writeValueAsString(transferDetails));
                                bankTransferStatus = transferStatusCode;
                            }
                            result.put("statusCode", meterTokenStatusCode);
                            result.put("message",meterTokenMessage);
                            result.put("statusMessage", "Successful");
                            result.put("initialMessage", meterTokenResult);
                            result.put("reference", operationId);
                            result.put("token", meterToken);
                            result.put("bankTransferStatus", bankTransferStatus);
                        }
                        else{
                            result.put("statusCode", "1");
                            result.put("message","Insufficient Wallet Balance");
                            result.put("statusMessage", "Failed");
                            result.put("initialMessage", meterTokenResult);
                            result.put("reference", operationId);
                            result.put("token", "");
                            result.put("bankTransferStatus", bankTransferStatus);
                        }
                    }
                    else{
                        result.put("statusCode", "1");
                        result.put("message","Invalid Service Code");
                        result.put("statusMessage", "Failed");
                        result.put("initialMessage", "");
                        result.put("reference", operationId);
                        result.put("token", "");
                        result.put("bankTransferStatus", "3");
                    }
                }
                else{
                    result.put("statusCode", "1");
                    result.put("message","Insufficient Wallet Balance");
                    result.put("statusMessage", "Failed");
                    result.put("initialMessage", "");
                    result.put("reference", operationId);
                    result.put("token", "");
                    result.put("bankTransferStatus", "3");
                }
            }
            else{
                result.put("statusCode", "1");
                result.put("message",customerWalletBalance.get("message"));
                result.put("statusMessage", "Failed");
                result.put("initialMessage", "");
                result.put("reference", operationId);
                result.put("token", "");
                result.put("bankTransferStatus", "3");
            }
        }
        else{
            result.put("statusCode", "1");
            result.put("message",dailyTxnBalanceMessage);
            result.put("statusMessage", "Failed");
            result.put("initialMessage", "");
            result.put("reference", operationId);
            result.put("token", "");
            result.put("bankTransferStatus", "3");
        }
        return result;
    }

    public Object getCustomerTxnFullDetails(String referenceId){
        HashMap<String,Object> result = new HashMap<>();
        List<Map<String, Object>> getAllTransactionFullDetails = sqlQueries.getCustomerTransactionFullDetails(referenceId);
        if(getAllTransactionFullDetails.isEmpty()){
            result.put("statusCode", "1");
            result.put("message", "No record Found");
            return result;
        }
        Map<String, Object> transactionFullDetails = getAllTransactionFullDetails.get(0);
        String tableName = String.valueOf(transactionFullDetails.get("table_name"));
        String serviceAccountNumber = String.valueOf(transactionFullDetails.get("service_account_no"));
        String customerId = String.valueOf(transactionFullDetails.get("customer_id"));

        Object resultOne = new Object();
        if(serviceAccountNumber.equals("1000000026") || serviceAccountNumber.equals("1000000031")){
            List<Map<String, Object>> getEstatePowerTxnLog = sqlQueries.estatePowerTxnLogs(customerId,referenceId);
            if(!getEstatePowerTxnLog.isEmpty()){
                resultOne = getEstatePowerTxnLog.get(0);
            }
        } else if (serviceAccountNumber.equals("10000000311")) {
            Optional<AutoPrivatePowerLogEntity> getEstatePowerTxnLog = autoPrivatePowerLogRepo.getEstatePowerTxnLog(customerId,referenceId);
            if(getEstatePowerTxnLog.isPresent()){
                resultOne = getEstatePowerTxnLog.get();
            }
        } else if (serviceAccountNumber.equals("1000000028")) {
            Optional<PosPaymentNotificationEntity> getCustomerReference = posPaymentNotificationRepo.customerTransactionReference(customerId,referenceId);
            if(getCustomerReference.isPresent()){
                resultOne = getCustomerReference.get();
            }
        } else if (serviceAccountNumber.equals("1000000021")) {
            String[] value = referenceId.split("_");
            referenceId = value[0];
            Optional<FundTransferTxnLogEntity> findByOperationId = fundTransferTxnLogRepo.findByOperationId(referenceId);
            if(findByOperationId.isPresent()){
                resultOne = findByOperationId.get();
            }
        }
        else if (serviceAccountNumber.equals("1000000023")){
            Optional<ProvidusSettlementNotificationEntity> getTransactionDetails =providusSettlementNotificationRepo.getCustomerTransaction(customerId,referenceId);
            if(getTransactionDetails.isPresent()){
                String body = getTransactionDetails.get().getBody();
                JsonObject jsonObject = new Gson().fromJson(body, JsonObject.class);
                String sourceAccountName = jsonObject.get("sourceAccountName").getAsString();
                String sourceBankName = jsonObject.get("sourceBankName").getAsString();
                HashMap<String, String> providusPayment = new HashMap<>();
                providusPayment.put("sourceAccountName", sourceAccountName);
                providusPayment.put("sourceBankName", sourceBankName);
                resultOne = providusPayment;
            }
        }
        else{
            String query = "SELECT * FROM " + tableName + " WHERE customer_id = :customerId AND operation_id = :referenceId";
            List resultList = entityManager.createNativeQuery(query)
                    .setParameter("customerId", customerId)
                    .setParameter("referenceId", referenceId)
                    .getResultList();

            if (!resultList.isEmpty()) {
                resultOne = utilityConfiguration.addColumnNamesToResultList((Object[]) resultList.get(0),tableName,entityManager);
            }
        }

        result.put("statusCode", "0");
        result.put("customer_wallet_result",transactionFullDetails);
        result.put("service_result", resultOne);
        result.put("message", "Successful");
        return result;
    }

    public Object customerWalletHistory(String phoneNumber, String serviceAccountNumber, String operationType, String startDate, String endDate){
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT cws.id, sat.service_name service_account_name, cws.reference_id, cws.operation_type, cws.operation_summary, ")
                .append("cws.amount, cws.commision_charge, cws.amount_charge, cws.wallet_balance, cws.status, cws.operation_at ")
                .append("FROM customer_wallets cws, service_accounts sat ")
                .append("WHERE cws.customer_id = :phoneNumber ")
                .append("AND cws.service_account_no = sat.service_account_no ")
                .append("AND DATE(cws.operation_at) BETWEEN :startDate AND :endDate ");
        if (serviceAccountNumber != null && !serviceAccountNumber.isEmpty()) {
            queryBuilder.append("AND cws.service_account_no = :serviceAccountNumber ");
        }
        if (operationType != null && !operationType.isEmpty()) {
            queryBuilder.append("AND cws.operation_type = :operationType ");
        }

        queryBuilder.append("ORDER BY cws.id DESC");
        Query query = entityManager.createNativeQuery(queryBuilder.toString())
                .setParameter("phoneNumber", phoneNumber)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);

        if (serviceAccountNumber != null && !serviceAccountNumber.isEmpty()) {
            query.setParameter("serviceAccountNumber", serviceAccountNumber);
        }
        if (operationType != null && !operationType.isEmpty()) {
            query.setParameter("operationType", operationType);
        }
        List<Object[]> sqlQueryResult = query.getResultList();

        List<Map<String, Object>> resultList = sqlQueryResult.stream().map(column -> {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("id", column[0]);
            resultMap.put("service_account_name", column[1]);
            resultMap.put("reference_id", column[2]);
            resultMap.put("operation_type", column[3]);
            resultMap.put("operation_summary", column[4]);
            resultMap.put("amount", column[5]);
            resultMap.put("commision_charge", column[6]);
            resultMap.put("amount_charge", column[7]);
            resultMap.put("wallet_balance", column[8]);
            resultMap.put("status", column[9]);
            resultMap.put("operation_at", column[10]);
            return resultMap;
        }).collect(Collectors.toList());

        return resultList;
    }
}
