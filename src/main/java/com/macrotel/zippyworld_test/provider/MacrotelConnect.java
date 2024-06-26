package com.macrotel.zippyworld_test.provider;

import com.macrotel.zippyworld_test.config.ThirdPartyAPI;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

public class MacrotelConnect {
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
        String airtimeUrl =  END_POINT_TLS+networkUrl;
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", X_API_KEY_TLS);
        headers.put("client-id", CLIENT_ID_TLS);
        headers.put("Content-Type", " application/x-www-form-urlencoded");

        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("phonenumber", phoneNumber);
        formParams.add("amount", String.valueOf(amount));
        formParams.add("transaction_id", transactionId);

        Object airtimeVendingThirdParty = thirdPartyAPI.callAPI(airtimeUrl, HttpMethod.POST,headers,formParams);
        if(airtimeVendingThirdParty == null){
            result.put("statusCode", "1");
            result.put("details", new ArrayList<>());
        }
        else{
            Map<String, Object> apiResponse = (Map<String, Object>) airtimeVendingThirdParty;
            String statusCode = (String) apiResponse.get("status_code");
            if(Objects.equals(statusCode, "0")){
                result.put("statusCode", "0");
                result.put("details", apiResponse);
            }
            else{
                result.put("statusCode", "1");
                result.put("details", apiResponse);
            }
        }
        return result;
    }

    public Object dataVendingRequest(String network, String phoneNumber, double amount, String planCode, String operationId){
        List<Object> result = new ArrayList<>();
        int dataAmount = (int) amount;
        if(Objects.equals(network, "MTN")){
            result.add(this.dataVending(phoneNumber,dataAmount,planCode,operationId,"mtn_bundle_vending"));
        } else if (Objects.equals(network,"AIRTEL")) {
            result.add(this.dataVending(phoneNumber,dataAmount,planCode,operationId,"airtel_bundle_vending"));
        }
        else if (Objects.equals(network,"9MOBILE")) {
            result.add(this.dataVending(phoneNumber,dataAmount,planCode,operationId,"nine_mobile_bundle_vending"));
        }
        else if (Objects.equals(network,"GLO")) {
            result.add(this.dataVending(phoneNumber,dataAmount,planCode,operationId,"glo_bundle_vending"));
        }
        return result;
    }

    private Object dataVending(String phoneNumber, int amount, String planCode, String transactionId, String networkUrl){
        HashMap<String, Object> result = new HashMap<>();
        String airtimeUrl =  END_POINT_TLS+networkUrl;
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", X_API_KEY_TLS);
        headers.put("client-id", CLIENT_ID_TLS);
        headers.put("Content-Type", " application/x-www-form-urlencoded");

        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("phonenumber", phoneNumber);
        formParams.add("amount", String.valueOf(amount));
        formParams.add("plan_code", planCode);
        formParams.add("transaction_id", transactionId);

        Object dataVendingThirdParty = thirdPartyAPI.callAPI(airtimeUrl, HttpMethod.POST,headers,formParams);
        if(dataVendingThirdParty == null){
            result.put("statusCode", "1");
            result.put("details", new ArrayList<>());
        }
        else{
            Map<String, Object> apiResponse = (Map<String, Object>) dataVendingThirdParty;
            String statusCode = (String) apiResponse.get("status_code");
            if(Objects.equals(statusCode, "0")){
                result.put("statusCode", "0");
                result.put("details", apiResponse);
            }
            else{
                result.put("statusCode", "1");
                result.put("details", apiResponse);
            }
        }
        return result;
    }

    public Object getBankList(){
        String bankListUrl = END_POINT_MS+"banks";
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", X_API_KEY_MS);
        headers.put("client-id", CLIENT_ID_MS);
        headers.put("Content-Type", " application/x-www-form-urlencoded");

        return thirdPartyAPI.callAPI(bankListUrl, HttpMethod.GET,headers,"");
    }

    public Object getBankAccountDetails(String bankCode, String accountNumber){
        HashMap<String, String> result = new HashMap<>();
        result.put("statusCode", "1");
        result.put("message", "No Record found");

        String bankAccountUrl =  END_POINT_MS+"bank_account_details";
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", X_API_KEY_MS);
        headers.put("client-id", CLIENT_ID_MS);
        headers.put("Content-Type", " application/x-www-form-urlencoded");

        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("account_number", accountNumber);
        formParams.add("bank_code", bankCode);

        Object bankAccountDetailsThirdParty = thirdPartyAPI.callAPI(bankAccountUrl, HttpMethod.POST,headers,formParams);
        if(bankAccountDetailsThirdParty != null){
            Map<String, Object> apiResponse = (Map<String, Object>) bankAccountDetailsThirdParty;
            String statusCode = String.valueOf(apiResponse.get("status_code"));
            if(!statusCode.equals("1")){
                Object accountResult = apiResponse.get("result");
                Map<String, Object> accountResultMap = (Map<String, Object>) accountResult;
                String accountName = String.valueOf(accountResultMap.get("accountName"));
                result.put("statusCode", "0");
                result.put("message", accountName);
            }
        }
        return result;
    }
    public Object bankTransferEp(String operationId, String accountNumber, String bankCode, double amount, String accountName, String senderPhoneNumber, String senderName, String narration){
        HashMap<String, Object> result = new HashMap<>();
        String airtimeUrl =  END_POINT_MS+"banktransfer";
        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", X_API_KEY_MS);
        headers.put("client-id", CLIENT_ID_MS);
        headers.put("Content-Type", " application/x-www-form-urlencoded");

        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("transaction_id", operationId);
        formParams.add("account_number", accountNumber);
        formParams.add("account_name", accountName);
        formParams.add("bank_code", bankCode);
        formParams.add("amount", String.valueOf(amount));
        formParams.add("sender_phonenumber", senderPhoneNumber);
        formParams.add("sender_names", senderName);
        formParams.add("narration", narration);

        Object bankTransferThirdParty = thirdPartyAPI.callAPI(airtimeUrl, HttpMethod.POST,headers,formParams);
        if(bankTransferThirdParty == null){
            result.put("statusCode", "4");
            result.put("details", new ArrayList<>());
        }
        else{
            Map<String, Object> apiResponse = (Map<String, Object>) bankTransferThirdParty;
            String statusCode = String.valueOf(apiResponse.get("responsecode"));
                result.put("statusCode", statusCode);
                result.put("details", apiResponse);
        }
        return result;
    }
}
