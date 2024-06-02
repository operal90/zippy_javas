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

    public Object getCustomerDetails (String meterNumber){
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
        String baseUrl = HES_LIVE_BASE_URL+"GetCustomer";
        Object getCustomerInformationAPI = thirdPartyAPI.callAPI(baseUrl, HttpMethod.POST,headers,apiParameters);
        Map<Object, String> customerInformationResponse = (Map<Object, String>) getCustomerInformationAPI;

        if(customerInformationResponse != null){
            String requestIdResponse = customerInformationResponse.get("reqId");
            long timeStampResponse = Long.parseLong(String.valueOf(customerInformationResponse.get("timestamp")))/1000;
            String dataResponse = customerInformationResponse.get("data");
            long currentResponseTimeStamp = Long.parseLong(utilityConfiguration.currentTimeStamp());
            long timeDifference =  currentResponseTimeStamp -  timeStampResponse;

            if(timeDifference < 15){
                Object decryptData = utilityConfiguration.decryptData(dataResponse);
                Map<String, String> decryptResponse = (Map<String, String>) decryptData;
                boolean result = Boolean.parseBoolean(String.valueOf(decryptResponse.get("Result")));
                if(!result){
                    response.put("statusCode", "1");
                    response.put("message", decryptResponse.get("msg"));
                    response.put("result", decryptResponse);
                }
                else{
                    decryptResponse.remove("msg");
                    decryptResponse.remove("Result");
                    response.put("statusCode", "0");
                    response.put("message", "Successful");
                    response.put("result", decryptResponse);
                }
            }
            else {
                response.put("statusCode", "1");
                response.put("message", "Network Error, Kindly retry");
                response.put("result",  new Object[0]);
            }
        }
        return response;
    }
}
