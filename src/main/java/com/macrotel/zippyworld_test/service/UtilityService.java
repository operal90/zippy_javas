package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import com.macrotel.zippyworld_test.entity.NetworkTxnLogEntity;
import com.macrotel.zippyworld_test.entity.SettingEntity;
import com.macrotel.zippyworld_test.pojo.UtilityResponse;
import com.macrotel.zippyworld_test.repo.NetworkTxnLogRepo;
import com.macrotel.zippyworld_test.repo.SettingRepo;
import com.macrotel.zippyworld_test.repo.SqlQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.macrotel.zippyworld_test.config.AppConstants.*;
@Service
public class UtilityService {
    UtilityResponse utilityResponse = new UtilityResponse();
    UtilityConfiguration utilityConfiguration = new UtilityConfiguration();
    @Autowired
    SqlQueries sqlQueries;
    @Autowired
    SettingRepo settingRepo;


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
                 float bmCommission = utilityConfiguration.formattedAmount(String.valueOf(bmCommissionPercent/100 * amount));
                 float zmCommission = utilityConfiguration.formattedAmount(String.valueOf(zonalManager /100 * amount));

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

                 float boCommission = utilityConfiguration.formattedAmount(String.valueOf(businessOwner/100 * amount));
                 float bmCommission = utilityConfiguration.formattedAmount(String.valueOf(businessManager/100 *amount));
                 float zmCommission = utilityConfiguration.formattedAmount(String.valueOf(zonalManager /100 *amount));
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
        Object[] customerCommission = getCustomerCommission.get(0);
        String cmt = customerCommission[0].toString();
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
    private Object getSettingValue(String name){
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
            LocalDateTime sessionTime = LocalDateTime.parse(customerSession[0].toString());
            LocalDateTime currentTime = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));

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
                                  String operationCode, String operationSummary, String value){
        HashMap<String, String> result = new HashMap<>();
        String providerResponse = "";
        String details = "";
        String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));

        return result;
    }

    public Object checkDailyTxnBalance(String customerId, double txnAmount, String serviceAccountNumber){
        HashMap<String, String> response = new HashMap<>();
        List<Object[]> getCustomerKycAmount = sqlQueries.getKycAmount(customerId);
        Object[] customerKycAmount = getCustomerKycAmount.get(0);
        String customerName = (String) customerKycAmount[0];
        String restriction = (String) customerKycAmount[3];
        String kycLevel = (String) customerKycAmount[2];
        if(!Objects.equals(restriction, "YES")){

        }
        return response;
    }

    public Object dailyTxnBalanceProcess()
}
