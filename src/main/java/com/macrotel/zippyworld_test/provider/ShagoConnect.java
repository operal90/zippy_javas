package com.macrotel.zippyworld_test.provider;

import com.macrotel.zippyworld_test.config.ThirdPartyAPI;
import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

public class ShagoConnect {
    ThirdPartyAPI thirdPartyAPI = new ThirdPartyAPI();
    UtilityConfiguration utilityConfiguration = new UtilityConfiguration();

    public Object electricityConnect(String operationId, String cardIdentity, double amount, String accountTypeId, String operatorId, String discoCode,
                                     String buyerPhoneNumber, String customerName,String customerAddress){
        HashMap<String, Object> result = new HashMap<>();
        String token= "";
        String resetToken = "";
        String configureToken = "";
        String tokenMessage = "";
        String formattedAmount = utilityConfiguration.numberFormat(String.valueOf(amount));
        accountTypeId = Objects.equals(accountTypeId, "1") ? "PREPAID" : "POSTPAID";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", " application/json");
        headers.put("hashKey", SHAGO_HASHKEY);

        HashMap<String, String> jsonData = new HashMap<>();
        jsonData.put("serviceCode", "AOB");
        jsonData.put("disco", discoCode);
        jsonData.put("meterNo", cardIdentity);
        jsonData.put("type", accountTypeId);
        jsonData.put("amount", Double.toString(amount));
        jsonData.put("phonenumber", buyerPhoneNumber);
        jsonData.put("name", customerName);
        jsonData.put("address", customerAddress);
        jsonData.put("request_id", operationId);
        Object electricityVending = thirdPartyAPI.callAPI(SHAGO_LIVE_BASE_URL, HttpMethod.POST,headers,jsonData);

        Map<String, Object> apiResponse = (Map<String, Object>) electricityVending;
        System.out.println(apiResponse);
        if(apiResponse != null) {
            String responseCode = (String) apiResponse.get("status");
            String message= (String) apiResponse.get("message");
            if(Objects.equals(responseCode, "200")){
                 token = (String) apiResponse.get("token");
                boolean hasConfigureToken = apiResponse.containsKey("configureToken");
                boolean hasResetToken = apiResponse.containsKey("resetToken");
                 configureToken = hasConfigureToken ? (String) apiResponse.get("configureToken") : "";
                 resetToken = hasResetToken ? (String) apiResponse.get("resetToken") : "";

                 if(!Objects.equals(configureToken,"") && !Objects.equals(resetToken,"")){
                     token = (String) apiResponse.get("token");
                     tokenMessage = " CONFIGURE TOKEN: "+configureToken+" , RESET TOKEN: "+resetToken+" , MAIN TOKEN:"+token;
                 }
                 else{
                     token = (String) apiResponse.get("token");
                     tokenMessage ="TOKEN: "+token;
                 }
                 result.put("statusCode", "0");
                 result.put("token", tokenMessage);
                 result.put("message", message);
                 result.put("statusMessage", "Successful");
            } else if (Objects.equals(responseCode, "300")) {
                result.put("statusCode", "1");
                result.put("token", "");
                result.put("message", message);
                result.put("statusMessage", "Pending");
            }
            else if (Objects.equals(responseCode, "400")) {
                result.put("statusCode", "2");
                result.put("token", "");
                result.put("message", "Your electricity transaction of N"+formattedAmount+" is processing and taking longer than usual. Kindly check your transaction history in 5 minutes or you can reach out to the customer support");
                result.put("statusMessage", "Pending");
            }
            else if (Objects.equals(responseCode, "IP001")) {
                result.put("statusCode", "2");
                result.put("token", "");
                result.put("message", "IP not whitelisted");
                result.put("statusMessage", "Failed");
            }
        }
        else{
            result.put("statusCode", "2");
            result.put("token", "");
            result.put("message", "The Service is unavailable at the moment. Try again later");
            result.put("statusMessage", "Pending");
        }
        return result;
    }
}
