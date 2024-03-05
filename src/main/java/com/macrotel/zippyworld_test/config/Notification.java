package com.macrotel.zippyworld_test.config;

import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    ThirdPartyAPI thirdPartyAPI = new ThirdPartyAPI();
    public String smsNotification(String phoneNumber, String header, String message){
        String smsNotificationUrl = "http://notif.zworld.ng/api/v1/notification/sms_message1";
        Map<String, String> notificationHeader = new HashMap<>();
        notificationHeader.put("x-notification-token", "2aa1513c-8998-454e-9d52-fa95b47fb142");
        notificationHeader.put("Content-Type", "application/json");
        HashMap<String, String> phoneNumberParams = new HashMap<>();
        phoneNumberParams.put("from", header);
        phoneNumberParams.put("to", phoneNumber);
        phoneNumberParams.put("msg", message);
        Object smsNotification = thirdPartyAPI.callAPI(smsNotificationUrl, HttpMethod.POST,notificationHeader,phoneNumberParams);
        return null;
    }

    public String emailNotification(String emailAddress, String username, String subject, String message){
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
        Object smsNotification = thirdPartyAPI.callAPI(emailNotificationUrl, HttpMethod.POST,notificationHeader,emailNotificationParams);
        return null;
    }

    public String whatsappNotification(String phoneNumber, String header, String message){
        String whatsAppNotificationUrl = "http://notif.zworld.ng/api/v1/notification/dynamic_whatsapp_message";
        Map<String, String> notificationHeader = new HashMap<>();
        notificationHeader.put("x-notification-token", "2aa1513c-8998-454e-9d52-fa95b47fb142");
        notificationHeader.put("Content-Type", "application/json");
        HashMap<String, String> whatsAppParams = new HashMap<>();
        whatsAppParams.put("header", header);
        whatsAppParams.put("phonenumber", phoneNumber);
        whatsAppParams.put("content", message);
        Object smsNotification = thirdPartyAPI.callAPI(whatsAppNotificationUrl, HttpMethod.POST,notificationHeader,whatsAppParams);
        return null;
    }
}
