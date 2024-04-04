package com.macrotel.zippyworld_test.config;

import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    ThirdPartyAPI thirdPartyAPI = new ThirdPartyAPI();
    public Integer smsNotification(String phoneNumber, String header, String message){
        int result =1;
        String smsNotificationUrl = "http://notif.zworld.ng/api/v1/notification/sms_message1";
        Map<String, String> notificationHeader = new HashMap<>();
        notificationHeader.put("x-notification-token", "2aa1513c-8998-454e-9d52-fa95b47fb142");
        notificationHeader.put("Content-Type", "application/json");
        HashMap<String, String> phoneNumberParams = new HashMap<>();
        phoneNumberParams.put("from", header);
        phoneNumberParams.put("to", phoneNumber);
        phoneNumberParams.put("msg", message);
        Object smsNotification = thirdPartyAPI.callAPI(smsNotificationUrl, HttpMethod.POST,notificationHeader,phoneNumberParams);
        Map<String, String> response = (Map<String, String>) smsNotification;
        String statusCode = String.valueOf(response.get("status_code"));
        if ("0".equals(statusCode)) {
            result= 0;
        }
        return result;
    }

    public Integer emailNotification(String emailAddress, String username, String subject, String message){
        int result =1;
        String emailNotificationUrl = "http://notif.zworld.ng/api/v1/notification/email_campaign";
        Map<String, String> notificationHeader = new HashMap<>();
        notificationHeader.put("x-notification-token", "2aa1513c-8998-454e-9d52-fa95b47fb142");
        notificationHeader.put("Content-Type", "application/json");
        HashMap<String, String> emailNotificationParams = new HashMap<>();
        emailNotificationParams.put("email", emailAddress);
        emailNotificationParams.put("name", username);
        emailNotificationParams.put("subj", subject);
        emailNotificationParams.put("adlink", ".");
        emailNotificationParams.put("adcontent", message);
        emailNotificationParams.put("myadvert", ".");
        emailNotificationParams.put("reflink", ".");
        Object emailNotification = thirdPartyAPI.callAPI(emailNotificationUrl, HttpMethod.POST,notificationHeader,emailNotificationParams);
        Map<String, String> response = (Map<String, String>) emailNotification;
        String statusCode = String.valueOf(response.get("status_code"));
        if ("0".equals(statusCode)) {
            result= 0;
        }
        return result;
    }

    public Integer whatsappNotification(String phoneNumber, String header, String message){
        int result =1;
        String whatsAppNotificationUrl = "http://notif.zworld.ng/api/v1/notification/dynamic_whatsapp_message";
        Map<String, String> notificationHeader = new HashMap<>();
        notificationHeader.put("x-notification-token", "2aa1513c-8998-454e-9d52-fa95b47fb142");
        notificationHeader.put("Content-Type", "application/json");
        HashMap<String, String> whatsAppParams = new HashMap<>();
        whatsAppParams.put("header", header);
        whatsAppParams.put("phonenumber", phoneNumber);
        whatsAppParams.put("content", message);
        Object whatsAppNotification = thirdPartyAPI.callAPI(whatsAppNotificationUrl, HttpMethod.POST,notificationHeader,whatsAppParams);
        Map<String, String> response = (Map<String, String>) whatsAppNotification;
        String statusCode = String.valueOf(response.get("status_code"));
        if ("0".equals(statusCode)) {
            result= 0;
        }
        return result;
    }
}
