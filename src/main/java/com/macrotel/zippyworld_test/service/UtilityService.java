package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import com.macrotel.zippyworld_test.entity.SettingEntity;
import com.macrotel.zippyworld_test.pojo.UtilityResponse;
import com.macrotel.zippyworld_test.repo.SettingRepo;
import com.macrotel.zippyworld_test.repo.SqlQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        double zonalManager = (double) agentCommission[1];
        double businessManager = (double) agentCommission[2];
        double businessOwner = (double) agentCommission[3];
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
                 bo.put("commission", "0");
                 bo.put("process", "NO");

                 Map<String, Object> bm = new HashMap<>();
                 bm.put("agent_type", "BM");
                 bm.put("customer_id", customerId);
                 bm.put("commission", String.valueOf(bmCommission));
                 bm.put("process", "NO");

                 Map<String, Object> zm = new HashMap<>();
                 zm.put("agent_type", "ZM");
                 zm.put("customer_id", zmCustomerId);
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
                 bm.put("agent_type", "BM");
                 bm.put("customer_id", bmCustomerId);
                 bm.put("commission", String.valueOf(bmCommission));
                 bm.put("process", "YES");

                 Map<String, Object> zm = new HashMap<>();
                 zm.put("agent_type", "ZM");
                 zm.put("customer_id", zmCustomerId);
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
        List<Object[]> getPromoCommissionQuery = sqlQueries.getPromoCommissionProcess(serviceAccountNo);
        if (!getPromoCommissionQuery.isEmpty()) {
            Object[] promoCommissionQuery = getPromoCommissionQuery.get(0);
            String mode = promoCommissionQuery[7].toString();
            String type = promoCommissionQuery[3].toString();
            String typeId = promoCommissionQuery[4].toString();
            String packageId = promoCommissionQuery[5].toString();
            String value = promoCommissionQuery[6].toString();

            if (Objects.equals(type, "DATE_RANGE") &&
                    ((Objects.equals(mode, "SPECIFIC") && Objects.equals(typeId, userTypeId) && Objects.equals(packageId, userPackageId))
                            ||
                            (Objects.equals(mode, "NON_SPECIFIC") && Objects.equals(typeId, userTypeId) && Objects.equals(packageId, userPackageId)))) {
                if (Objects.equals(mode, "SPECIFIC") || Objects.equals(mode, "NON_SPECIFIC")) {
                    List<Object[]> getRegistrationDateDiff = sqlQueries.getRegistrationDateDiff(customerId);
                    Object[] registrationDateDiff = getRegistrationDateDiff.get(0);
                    String dateDiff = registrationDateDiff[0].toString();
                    if (Objects.equals(dateDiff, value)) {
                        result.put("statusCode", "0");
                        result.put("cmt", promoCommissionQuery[8].toString());
                        result.put("cmp", promoCommissionQuery[9].toString());
                        result.put("csc", promoCommissionQuery[10].toString());
                        result.put("msv", promoCommissionQuery[11].toString());
                    }
                }
            }
        }
        return result;
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
}
