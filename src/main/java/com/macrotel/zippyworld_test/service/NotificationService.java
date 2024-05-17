package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.config.ThirdPartyAPI;
import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import com.macrotel.zippyworld_test.entity.MessageServiceEntity;
import com.macrotel.zippyworld_test.entity.UserAccountEntity;
import com.macrotel.zippyworld_test.repo.MessageServiceRepo;
import com.macrotel.zippyworld_test.repo.UserAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.macrotel.zippyworld_test.config.AppConstants.NOTIFICATION_BASE_URL;

@Service
public class NotificationService {
    private final  UtilityService utilityService;
    private final LoggingService loggingService;
    ThirdPartyAPI thirdPartyAPI = new ThirdPartyAPI();
    public NotificationService(UtilityService utilityService, LoggingService loggingService){
        this.utilityService = utilityService;
        this.loggingService = loggingService;
    }
    UtilityConfiguration utilityConfiguration = new UtilityConfiguration();
    @Autowired
    MessageServiceRepo messageServiceRepo;
    @Autowired
    UserAccountRepo userAccountRepo;

    private Object userNotificationSubscribe(String customerId, String userTypeId, String userPackageId, String customerName){
        HashMap<String, Integer> userSubscribeResult = new HashMap<>();
        int sendEmail =0;
        int sendSms = 1;
        int sendWhatsApp = 1;
        String todayDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        //Get Notification User Subscribe for
        Optional<MessageServiceEntity> getUserNotificationSubscribe = messageServiceRepo.findByCustomerId(customerId);
        if(getUserNotificationSubscribe.isPresent()){
            MessageServiceEntity messageServiceEntity = getUserNotificationSubscribe.get();
            String emailSubscriber = messageServiceEntity.getEmail();
            String smsSubscriber = messageServiceEntity.getSms();
            String whatsAppSubscriber = messageServiceEntity.getWhatsapp();

            //Check User Balance before sending whatsapp and sms as whatsapp cost 5 naira and sms cost 4 naira
            Object getCustomerWalletBalance = utilityService.queryCustomerWalletBalance(customerId);
            Map<String, String> customerWalletBalance = (Map<String, String>) getCustomerWalletBalance;
            double customerWalletBalanceAmount = Double.parseDouble(customerWalletBalance.get("amount"));

            if(smsSubscriber.equals("0")){
                if(customerWalletBalanceAmount >= 4) {
                    customerWalletBalanceAmount -=4;
                    //Log the Transaction
                    String smsOperationId = utilityConfiguration.getOperationId("NOT");
                    String operationSummaryMessage = customerName +" paid N4.00 for SMS Message Notification";
                    loggingService.customerWalletLogging(smsOperationId,"MAIN","DR",userTypeId,userPackageId,"",operationSummaryMessage,
                            4,"","",0,4,customerId, utilityConfiguration.formattedAmount(String.valueOf(customerWalletBalanceAmount)),"Successful",todayDate);
                    sendSms = 0;
                }
            }

            if(whatsAppSubscriber.equals("0")){
                if(customerWalletBalanceAmount >= 5) {
                    customerWalletBalanceAmount -=5;
                    String whatsAppOperationId = utilityConfiguration.getOperationId("NOT");
                    String operationSummaryMessage = customerName +" paid N5.00 for Whatsapp Message Notification";
                    loggingService.customerWalletLogging(whatsAppOperationId,"MAIN","DR",userTypeId,userPackageId,"",operationSummaryMessage,
                            5,"","",0,5,customerId, utilityConfiguration.formattedAmount(String.valueOf(customerWalletBalanceAmount)),"Successful", todayDate);
                    sendWhatsApp = 0;
                }
            }
        }

        userSubscribeResult.put("emailYes", sendEmail);
        userSubscribeResult.put("smsYes", sendSms);
        userSubscribeResult.put("whatsAppYes", sendWhatsApp);
        return userSubscribeResult;
    }

    public void sendAirtimeNotification(String customerId, String userTypeId, String userPackageId, String customerName, String customerEmail, double amount){
        //Get user subscription
        Object userSubscription = this.userNotificationSubscribe(customerId,userTypeId,userPackageId,customerName);
        Map<String,Integer> getUserSubscription = (Map<String,Integer>) userSubscription;
        int emailYes = getUserSubscription.get("emailYes");
        int whatsappYes = getUserSubscription.get("whatsAppYes");
        int smsYes = getUserSubscription.get("smsYes");

        Map<String, String> notificationHeader = new HashMap<>();
        notificationHeader.put("Content-Type", "application/json");
        HashMap<String, Object> airtimeParameters = new HashMap<>();
        airtimeParameters.put("operation", "AIRTIME/DATA");
        airtimeParameters.put("customerName", customerName);
        airtimeParameters.put("walletNumber", customerId);
        airtimeParameters.put("email", customerEmail);
        airtimeParameters.put("amount", amount);
        airtimeParameters.put("emailYes", emailYes);
        airtimeParameters.put("whatsappYes", whatsappYes);
        airtimeParameters.put("smsYes", smsYes);
         thirdPartyAPI.callAPI(NOTIFICATION_BASE_URL, HttpMethod.POST,notificationHeader,airtimeParameters);
    }
}
