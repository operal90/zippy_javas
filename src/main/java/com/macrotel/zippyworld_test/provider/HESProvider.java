package com.macrotel.zippyworld_test.provider;

import com.macrotel.zippyworld_test.config.ThirdPartyAPI;
import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

public class HESProvider {
    ThirdPartyAPI thirdPartyAPI = new ThirdPartyAPI();
    UtilityConfiguration utilityConfiguration = new UtilityConfiguration();

    public Object getCustomerDetails(String meterNumber) {
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, String> encryptedArray = new HashMap<>();
        encryptedArray.put("meterNo", meterNumber);
        String encryptedData = utilityConfiguration.encryptData(encryptedArray);
        String requestTimeStamp = utilityConfiguration.currentTimeStamp();
        String requestId = utilityConfiguration.randomDigit(8);

        HashMap<String, String> apiParameters = new HashMap<>();
        apiParameters.put("reqId", requestId);
        apiParameters.put("timestamp", requestTimeStamp);
        apiParameters.put("data", encryptedData);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", " application/json");
        String baseUrl = HES_LIVE_BASE_URL + "GetCustomer";
        Object getCustomerInformationAPI = thirdPartyAPI.callAPI(baseUrl, HttpMethod.POST, headers, apiParameters);
        Map<Object, String> customerInformationResponse = (Map<Object, String>) getCustomerInformationAPI;

        if (customerInformationResponse != null) {
            String requestIdResponse = customerInformationResponse.get("reqId");
            long timeStampResponse = Long.parseLong(String.valueOf(customerInformationResponse.get("timestamp"))) / 1000;
            String dataResponse = customerInformationResponse.get("data");
            long currentResponseTimeStamp = Long.parseLong(utilityConfiguration.currentTimeStamp());
            long timeDifference = currentResponseTimeStamp - timeStampResponse;

            if (timeDifference < 15) {
                Object decryptData = utilityConfiguration.decryptData(dataResponse);
                Map<String, String> decryptResponse = (Map<String, String>) decryptData;
                boolean result = Boolean.parseBoolean(String.valueOf(decryptResponse.get("Result")));
                if (!result) {
                    response.put("statusCode", "1");
                    response.put("message", decryptResponse.get("msg"));
                    response.put("result", decryptResponse);
                } else {
                    decryptResponse.remove("msg");
                    decryptResponse.remove("Result");
                    response.put("statusCode", "0");
                    response.put("message", "Successful");
                    response.put("result", decryptResponse);
                }
            } else {
                response.put("statusCode", "1");
                response.put("message", "Network Error, Kindly retry");
                response.put("result", new Object[0]);
            }
        }
        return response;
    }

    public Object buyToken(String meterNumber, String customerName, Double amount, Integer loadingType) {
        HashMap<String, Object> tokenResponseResult = new HashMap<>();
        String message = "";
        String failedMessage = "Dear "+customerName+", your power token is unavailable now. Kindly contact the Admin. Thanks for using Zippyworld";
        String response = "";

        tokenResponseResult.put("statusCode", "1");
        tokenResponseResult.put("message", failedMessage);
        tokenResponseResult.put("response", response);
        tokenResponseResult.put("token", "");
        tokenResponseResult.put("result",  new Object [0]);

        HashMap<String, Object> encryptedArray = new HashMap<>();
        encryptedArray.put("meterNo", meterNumber);
        encryptedArray.put("user", customerName);
        encryptedArray.put("orderNo", utilityConfiguration.randomDigit(6));
        encryptedArray.put("val", amount);
        encryptedArray.put("toMeter", loadingType);
        String encryptedData = utilityConfiguration.encryptData(encryptedArray);
        String requestTimeStamp = utilityConfiguration.currentTimeStamp();
        String requestId = utilityConfiguration.randomDigit(8);

        HashMap<String, String> apiParameters = new HashMap<>();
        apiParameters.put("reqId", requestId);
        apiParameters.put("timestamp", requestTimeStamp);
        apiParameters.put("data", encryptedData);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", " application/json");
        String baseUrl = HES_LIVE_BASE_URL + "GetToken";
        Object getTokenAPI = thirdPartyAPI.callAPI(baseUrl, HttpMethod.POST, headers, apiParameters);
        Map<Object, String> tokenResponse = (Map<Object, String>) getTokenAPI;

        if (tokenResponse != null) {
            long timeStampResponse = Long.parseLong(String.valueOf(tokenResponse.get("timestamp"))) / 1000;
            String dataResponse = tokenResponse.get("data");
            long currentResponseTimeStamp = Long.parseLong(utilityConfiguration.currentTimeStamp());
            long timeDifference = currentResponseTimeStamp - timeStampResponse;

            if (timeDifference < 15) {
                Object decryptData = utilityConfiguration.decryptData(dataResponse);
                Map<String, Object> decryptResponse = (Map<String, Object>) decryptData;
                if(!decryptResponse.isEmpty()){
                    boolean result = Boolean.parseBoolean(String.valueOf(decryptResponse.get("Result")));
                    if (result) {
                        String sendRstCode =  String.valueOf(decryptResponse.get("SendRstCode"));
                        String token = String.valueOf(decryptResponse.get("Token"));
                        String energy = String.valueOf(decryptResponse.get("Energy"));

                        if(sendRstCode.equals("0") || sendRstCode.equals("0.0")){
                            message = "Dear "+customerName +", your power token is "+token+" ,which has been successfully sent to your meter. Thanks for using Zippyworld";
                            response ="0";
                        } else if (sendRstCode.equals("1") || sendRstCode.equals("1.0")) {
                            message = "Dear "+customerName +", your power token is "+token+" .Request Failed. Thanks for using Zippyworld";
                            response ="0";
                        }
                        else if (sendRstCode.equals("2") || sendRstCode.equals("2.0")) {
                            message = "Dear "+customerName +", your power token is "+token+" .Meter not registered, sending failed. Thanks for using Zippyworld";
                            response ="0";
                        }
                        else if (sendRstCode.equals("3")|| sendRstCode.equals("3.0")) {
                            message = "Dear "+customerName +", your power token is "+token+" .Meter not online, sending failed. Thanks for using Zippyworld";
                            response ="0";
                        }
                        else if (sendRstCode.equals("4") || sendRstCode.equals("4.0")) {
                            message = "Dear "+customerName +", your power token is "+token+" .Meter not responding, sending failed. Thanks for using Zippyworld";
                            response ="0";
                        }
                        else if (sendRstCode.equals("5") || sendRstCode.equals("5.0")) {
                            message = "Dear "+customerName +", your power token is "+token+" .Meter rejects the token. Thanks for using Zippyworld";
                            response ="0";
                        }
                        else{
                            message = "Dear "+customerName +", your power token is "+token+" .Thanks for using Zippyworld";
                            response ="1";
                        }
                        tokenResponseResult.put("statusCode", "0");
                        tokenResponseResult.put("message", message);
                        tokenResponseResult.put("token", token);
                        tokenResponseResult.put("response", response);
                        tokenResponseResult.put("result",  decryptResponse);
                    }
                }
            }
        }
        return tokenResponseResult;
    }
}

