package com.macrotel.zippyworld_test.provider;

import com.macrotel.zippyworld_test.config.AppConstants;
import com.macrotel.zippyworld_test.config.ThirdPartyAPI;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

public class TelecomConnect {
    ThirdPartyAPI thirdPartyAPI = new ThirdPartyAPI();
    public Object airtimeVendingRequest(String network, String phoneNumber, double amount, String operationId){
        int airtimeAmount = (int) amount;
        List<Object> result = new ArrayList<>();
        if(Objects.equals(network, "MTN")){
            result.add(this.airtimeVending(phoneNumber,airtimeAmount,operationId,"mtn_airtime_vending"));
        } else if (Objects.equals(network, "AIRTEL")) {
            result.add(this.airtimeVending(phoneNumber,airtimeAmount,operationId,"airtel_airtime_vending"));
        }
        else if (Objects.equals(network, "9MOBILE")) {
            result.add(this.airtimeVending(phoneNumber,airtimeAmount,operationId,"nine_mobile_airtime_vending"));
        }
        else if (Objects.equals(network, "GLO")) {
            result.add(this.airtimeVending(phoneNumber,airtimeAmount,operationId,"glo_airtime_vending"));
        }

        return result;
    }

    private Object airtimeVending(String phoneNumber, int amount, String transactionId, String networkUrl){
        HashMap<String, Object> result = new HashMap<>();
//        String airtimeUrl =  END_POINT_TLS+networkUrl;
//        Map<String, String> headers = new HashMap<>();
//        headers.put("x-api-key", X_API_KEY_TLS);
//        headers.put("client-id", CLIENT_ID_TLS);
//        headers.put("Content-Type", " application/x-www-form-urlencoded");
//
//
//
//        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
//        formParams.add("phonenumber", phoneNumber);
//        formParams.add("amount", String.valueOf(amount));
//        formParams.add("transaction_id", transactionId);
//
//        Object airtimeVendingThirdParty = thirdPartyAPI.callAPI(airtimeUrl, HttpMethod.POST,headers,formParams);
//        if(airtimeVendingThirdParty == null){
//            result.put("statusCode", "1");
//            result.put("details", new ArrayList<>());
//        }
//        else{
//            Map<String, Object> apiResponse = (Map<String, Object>) airtimeVendingThirdParty;
//            String statusCode = (String) apiResponse.get("status_code");
//            if(Objects.equals(statusCode, "0")){
//                result.put("statusCode", "0");
//                result.put("details", apiResponse);
//            }
//            else{
//                result.put("statusCode", "1");
//                result.put("details", apiResponse);
//            }
//        }
        HashMap<String, String> apiResponse = new HashMap<>();
        apiResponse.put("status_code","0");
        apiResponse.put("message", "Successful");
        apiResponse.put("reference_number", "2024033023209799");
        apiResponse.put("confirm_status","Successful");
        apiResponse.put("network", "Glo");
        result.put("statusCode", "0");
        result.put("details", apiResponse);
        return result;
    }
}
