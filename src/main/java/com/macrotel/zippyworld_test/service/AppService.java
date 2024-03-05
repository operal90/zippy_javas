package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.config.Notification;
import com.macrotel.zippyworld_test.config.ThirdPartyAPI;
import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import com.macrotel.zippyworld_test.dto.IdentityTypeDTO;
import com.macrotel.zippyworld_test.entity.*;
import com.macrotel.zippyworld_test.pojo.*;
import com.macrotel.zippyworld_test.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.macrotel.zippyworld_test.config.AppConstants.*;
@Service
public class AppService {

    BaseResponse baseResponse = new BaseResponse(true);
    ThirdPartyAPI thirdPartyAPI = new ThirdPartyAPI();
    UtilityConfiguration utilities = new UtilityConfiguration();
    Notification notification = new Notification();
    private static final Logger LOG = Logger.getLogger(AppService.class.getName());

    @Autowired
    UserAccountRepo userAccountRepo;
    @Autowired
    QRCustomerWalletRepo qrCustomerWalletRepo;
    @Autowired
    CustomerWalletRepo customerWalletRepo;
    @Autowired
    CustomerCommissionWalletRepo customerCommissionWalletRepo;
    @Autowired
    QRDetailRepo qrDetailRepo;
    @Autowired
    MessageServiceRepo messageServiceRepo;
    @Autowired
    IdentityRepo identityRepo;
    @Autowired
    SecurityQuestionRepo securityQuestionRepo;
    @Autowired
    OTPRepo otpRepo;
    @Autowired
    CustomerIdentityRecordRepo customerIdentityRecordRepo;


    public BaseResponse testing(){
        baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
        baseResponse.setMessage("API is working well");
        baseResponse.setResult(EMPTY_RESULT);
        return baseResponse;
    }
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse userCreation(UserCreationData userCreationData){
        try{
            //Check if Phone Number already exist
            Optional<UserAccountEntity> isPhoneNumberExist = userAccountRepo.findByPhonenumber(userCreationData.getPhonenumber());
            if(isPhoneNumberExist.isPresent()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Phonenumber already registered.");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Check if Identity Number already exist
            Optional<UserAccountEntity> isIdentityNumberExist  = userAccountRepo.findByIdentityNumber(userCreationData.getIdentityNumber());
            if(isIdentityNumberExist.isPresent()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Identity Number already registered.");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Check if Email Address already exist
            Optional<UserAccountEntity> isEmailExist = userAccountRepo.findByEmail(userCreationData.getEmail());
            if(isEmailExist.isPresent()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Email Address already registered.");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Check User Identity Type if exist
            Optional<IdentityEntity> isIdentityExist = identityRepo.findById(Long.parseLong(userCreationData.getIdentityId()));
            if(isIdentityExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Invalid Identity Type");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Check User Security Question Type if exist
            Optional<SecurityQuestionEntity> isSecurityQuestionExist = securityQuestionRepo.findById(Long.parseLong(userCreationData.getUuid()));
            if(isSecurityQuestionExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Invalid Security Question Id");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Convert User FirstName and Lastname to generate Account Name, get TransactionId, ReferenceId
            String accountName = userCreationData.getFirstname() +' '+ userCreationData.getLastname();
            String referenceId = utilities.refernceId();

            //Generate Account Number
//            String identityUrl = "https://vps.providusbank.com/vps/api/PiPCreateReservedAccountNumber";
//            Map<String, String> headers = new HashMap<>();
//            headers.put("Client-Id", "bUBjUjBUM0xfUHIpKCkuTTEyMw==");
//            headers.put("X-Auth-Signature", " 9d5f1854bc0ebb9efa378354a5468ee156ae03c5265687c95cf9173d8eb62c1beb4ca616c40c35a55dd38a9a93415c3c98999f9d67020a1bc278bd3db23f26fc");
//            headers.put("Content-Type", " application/json");
//            HashMap<String, String> formParams = new HashMap<>();
//            formParams.put("account_name", accountName);
//            formParams.put("bvn", userCreationData.getIdentityNumber());
//            Object generateAccountNumber = thirdPartyAPI.callAPI(identityUrl, HttpMethod.POST,headers,formParams);
//
//            String accountNumber = userCreationData.getPhonenumber();
//            String userAccountName = accountName;
//            Map<String, Object> apiResponse = (Map<String, Object>) generateAccountNumber;
//            if(generateAccountNumber != null){
//                String responseCode = (String) apiResponse.get("responseCode");
//                if(Objects.equals(responseCode, "00")) {
//                    accountNumber = (String) apiResponse.get("account_number");
//                    userAccountName = (String) apiResponse.get("account_name");
//                }
//            }
            //Insert into User Account Table
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setFirstname(userCreationData.getFirstname());
            userAccountEntity.setLastname(userCreationData.getLastname());
            userAccountEntity.setPhonenumber(userCreationData.getPhonenumber());
            userAccountEntity.setAccountNo("000000000");
            userAccountEntity.setAccountName("userAccountName");
            userAccountEntity.setGender(userCreationData.getGender());
            userAccountEntity.setPromoCode(userCreationData.getPromo_code());
            userAccountEntity.setReferrerCode(userCreationData.getReferrer_code());
            userAccountEntity.setReferralPhonenumber(userCreationData.getReferral_phonenumber());
            userAccountEntity.setIdentityId(userCreationData.getIdentityId());
            userAccountEntity.setIdentityNumber(userCreationData.getIdentityNumber());
            userAccountEntity.setStatus("0");
            userAccountEntity.setWtlStatus("2");
            userAccountEntity.setKycLevel("1");
            userAccountEntity.setParentAggregatorCode(userCreationData.getAggregator_code());
            userAccountEntity.setGender(userCreationData.getGender());
            userAccountEntity.setEmail(userCreationData.getEmail());
            userAccountEntity.setPin(utilities.shaEncryption(userCreationData.getPin()));
            userAccountEntity.setTxnPin(utilities.shaEncryption("001122"));
            userAccountEntity.setSecureId(userCreationData.getSecure_id());
            userAccountEntity.setAnswer(utilities.shaEncryption(userCreationData.getAnswer()));
            userAccountEntity.setUserType("1");
            userAccountEntity.setUserPackageId("1");
            userAccountEntity.setAgreed(userCreationData.getAgreed());
            userAccountEntity.setCommissionMode("INSTANCE");
            userAccountEntity.setOperationId(referenceId);
            userAccountEntity.setIdentityPhonenumber(userCreationData.getIdentity_phonenumber());
            userAccountRepo.save(userAccountEntity);

            //Insert into Customer Wallet
            CustomerWalletEntity customerWalletEntity = new CustomerWalletEntity();
            customerWalletEntity.setReferenceId(referenceId);
            customerWalletEntity.setOperationEvent("MAIN");
            customerWalletEntity.setOperationType("CR");
            customerWalletEntity.setServiceAccountNo("1000000020");
            customerWalletEntity.setOperationSummary("Credit new customer account");
            customerWalletEntity.setAmount((double) 0);
            customerWalletEntity.setAmountCharge((double) 0);
            customerWalletEntity.setCommisionType("NN");
            customerWalletEntity.setCommisionCharge((double) 0);
            customerWalletEntity.setCustomerId(userCreationData.getPhonenumber());
            customerWalletEntity.setWalletBalance((double) 0);
            customerWalletEntity.setUserTypeId("1");
            customerWalletEntity.setStatus("Successful");
            customerWalletRepo.save(customerWalletEntity);

            //Insert into Customer QR Wallet
            QRCustomerWalletEntity qrCustomerWalletEntity = new QRCustomerWalletEntity();
            qrCustomerWalletEntity.setReferenceId(referenceId);
            qrCustomerWalletEntity.setOperationEvent("MAIN");
            qrCustomerWalletEntity.setOperationType("CR");
            qrCustomerWalletEntity.setServiceAccountNo("1000000020");
            qrCustomerWalletEntity.setOperationSummary("Credit new customer account");
            qrCustomerWalletEntity.setAmount((double) 0);
            qrCustomerWalletEntity.setAmountCharge((double) 0);
            qrCustomerWalletEntity.setCommisionType("NN");
            qrCustomerWalletEntity.setCommisionCharge((double) 0);
            qrCustomerWalletEntity.setCustomerId(userCreationData.getPhonenumber());
            qrCustomerWalletEntity.setWalletBalance((double) 0);
            qrCustomerWalletEntity.setUserTypeId("1");
            qrCustomerWalletEntity.setStatus("Successful");
            qrCustomerWalletRepo.save(qrCustomerWalletEntity);

            //Insert Into Customer Wallet Commission
            CustomerCommissionWalletEntity customerCommissionWalletEntity = new CustomerCommissionWalletEntity();
            customerCommissionWalletEntity.setReferenceId(referenceId);
            customerCommissionWalletEntity.setOperationType("CR");
            customerCommissionWalletEntity.setOperationSummary("Credit new customer account");
            customerCommissionWalletEntity.setWalletBalance((double) 0);
            customerCommissionWalletEntity.setCustomerId(userCreationData.getPhonenumber());
            customerCommissionWalletRepo.save(customerCommissionWalletEntity);

            //Insert Into QR Table
            QRDetailsEntity qrDetailsEntity = new QRDetailsEntity();
            qrDetailsEntity.setCustomerId(userCreationData.getPhonenumber());
            qrDetailsEntity.setStatus("1");
            qrDetailsEntity.setTokenReceiver(userCreationData.getPhonenumber());
            qrDetailRepo.save(qrDetailsEntity);

            //Insert into Message Service
            MessageServiceEntity messageServiceEntity = new MessageServiceEntity();
            messageServiceEntity.setCustomerId(userCreationData.getPhonenumber());
            messageServiceEntity.setEmail("0");
            messageServiceEntity.setSms("1");
            messageServiceEntity.setWhatsapp("1");
            messageServiceRepo.save(messageServiceEntity);

            //Insert into Customer Identity Record
            CustomerIdentityRecordEntity customerIdentityRecord = new CustomerIdentityRecordEntity();
            customerIdentityRecord.setIdentityName(userCreationData.getFirstname()+" "+userCreationData.getLastname());
            customerIdentityRecord.setCustomerId(userCreationData.getPhonenumber());
            customerIdentityRecord.setKycId(userCreationData.getIdentityId());
            customerIdentityRecord.setIdentityNumber(userCreationData.getIdentityNumber());
            customerIdentityRecordRepo.save(customerIdentityRecord);

            baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
            baseResponse.setMessage("Account Created Successful");
            baseResponse.setResult(EMPTY_RESULT);
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
            throw new RuntimeException("Error during user creation", ex);
        }
        return baseResponse;
    }

    public BaseResponse createIdentityType(IdentityData identityData){
        try{
            //Check if Identity Type name already exist
            Optional<IdentityEntity> isIdentityExist = identityRepo.findByIdentityType(identityData.getIdentityName());
            if(isIdentityExist.isPresent()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Identity Name Already exist");
                baseResponse.setResult(EMPTY_RESULT);
                return  baseResponse;
            }
            //Save The new Identity Name
            IdentityEntity identityEntity = new IdentityEntity(identityData);
            identityRepo.save(identityEntity);

            baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
            baseResponse.setMessage("Identity Type Created Successful");
            baseResponse.setResult(EMPTY_RESULT);
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    public BaseResponse listIdentityType(){
        try{
            List<IdentityEntity> getIdentityList = identityRepo.findAll();
            List<Object> result = new ArrayList<>();
            for(IdentityEntity identityEntity :  getIdentityList){
                IdentityTypeDTO identityTypeDTO = new IdentityTypeDTO();
                identityTypeDTO.setId(String.valueOf(identityEntity.getId()));
                identityTypeDTO.setIdentityType(identityEntity.getIdentityType());
                result.add(identityTypeDTO);
            }
            baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
            baseResponse.setMessage(SUCCESS_MESSAGE);
            baseResponse.setResult(result);
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    public BaseResponse listSecurityQuestion(){
        try{
            List<SecurityQuestionEntity> getAllSecurityQuestion = securityQuestionRepo.findAll();
            List<Object> result = new ArrayList<>();
            for(SecurityQuestionEntity securityQuestionEntity : getAllSecurityQuestion){
                HashMap<String, String> securityQuestionMap = new HashMap<>();
                securityQuestionMap.put("id", String.valueOf(securityQuestionEntity.getId()));
                securityQuestionMap.put("question", securityQuestionEntity.getQuestion());
                result.add(securityQuestionMap);
            }
            baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
            baseResponse.setMessage(SUCCESS_MESSAGE);
            baseResponse.setResult(result);
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    public BaseResponse verifyUserIdentity(VerifyUserIdentityData verifyUserIdentityData){
        try{
            //Is Identity Type exist
            Optional<IdentityEntity> isIdentityExist = identityRepo.findById(Long.parseLong(verifyUserIdentityData.getIdentityId()));
            if(isIdentityExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Invalid Identity Id");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            String bvnUrl = "https://zippyworld.com.ng:8443/macrotel_info_verification_services/api/bvn";
            String ninUrl = "https://zippyworld.com.ng:8443/macrotel_info_verification_services/api/nin";
            Map<String, String> headers = new HashMap<>();
            headers.put("x-api-key", "7816024");
            headers.put("client-id", " YY590KH-452-FGB");
            headers.put("Content-Type", " application/x-www-form-urlencoded");

            //NIN is 1, BVN is 2.
            String identityId = verifyUserIdentityData.getIdentityId();
            if(Objects.equals(identityId, "1")){
                MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
                formParams.add("nin", verifyUserIdentityData.getIdentityNumber());
                formParams.add("phonenumber", verifyUserIdentityData.getPhoneNumber());
                Object getUserBVNData = thirdPartyAPI.callAPI(ninUrl, HttpMethod.POST,headers,formParams);
                if(getUserBVNData == null){
                    baseResponse.setStatus_code(ERROR_STATUS_CODE);
                    baseResponse.setMessage("NIN API Server down");
                    baseResponse.setResult(EMPTY_RESULT);
                }
                else{
                    Map<String, Object> apiResponse = (Map<String, Object>) getUserBVNData;
                    String statusCode = (String) apiResponse.get("status_code");
                    if(Objects.equals(statusCode, "1")){
                        baseResponse.setStatus_code(ERROR_STATUS_CODE);
                        baseResponse.setMessage("No result found for this NIN");
                        baseResponse.setResult(EMPTY_RESULT);
                    }
                    else{
                        baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
                        baseResponse.setMessage(SUCCESS_MESSAGE);
                        baseResponse.setResult(apiResponse.get("result"));
                    }
                }

            }
            if(Objects.equals(identityId, "2")){
                MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
                formParams.add("bvn", verifyUserIdentityData.getIdentityNumber());
                formParams.add("phonenumber", verifyUserIdentityData.getPhoneNumber());
                Object getUserBVNData = thirdPartyAPI.callAPI(bvnUrl, HttpMethod.POST,headers,formParams);
                if(getUserBVNData == null){
                    baseResponse.setStatus_code(ERROR_STATUS_CODE);
                    baseResponse.setMessage("BVN API Server down");
                    baseResponse.setResult(EMPTY_RESULT);
                }
                else{
                    Map<String, Object> apiResponse = (Map<String, Object>) getUserBVNData;
                    String statusCode = (String) apiResponse.get("status_code");
                    if(Objects.equals(statusCode, "1")){
                        baseResponse.setStatus_code(ERROR_STATUS_CODE);
                        baseResponse.setMessage("No result found for this BVN");
                        baseResponse.setResult(EMPTY_RESULT);
                    }
                    else{
                        baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
                        baseResponse.setMessage(SUCCESS_MESSAGE);
                        baseResponse.setResult(apiResponse.get("result"));
                    }
                }

            }
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    public BaseResponse generateRegistrationOTPCode(NotificationData notificationData){
        try{
            String phoneNumber = notificationData.phoneNumber;
            String emailAddress = notificationData.emailAddress;
            String username = utilities.extractUsername(emailAddress);
            String userOtp = utilities.otpCode(7);
            OTPEntity otpEntity = new OTPEntity();
            otpEntity.setToken1(userOtp);
            otpEntity.setToken(utilities.shaEncryption(userOtp));
            otpEntity.setCustomerId(notificationData.getPhoneNumber());
            otpEntity.setEmail(notificationData.getEmailAddress());
            otpEntity.setOperationType("REGISTRATION");
            otpRepo.save(otpEntity);

            String notificationMessage = "Welcome to Zippyworld! " +
                                        "Your One-Time Passcode (OTP) is: "+userOtp+"." +
                                        " Use it to complete registration securely. Reach out to support if assistance is needed. Thank you for joining us!";
            String smsNotification = notification.smsNotification(phoneNumber, "Zippyworld", notificationMessage);
            String emailNotification = notification.emailNotification(emailAddress,username,"Zippyworld", notificationMessage);
            String whatsAppNotification = notification.whatsappNotification(phoneNumber, "Zippyworld", notificationMessage);
            HashMap<String,String> result = new HashMap<>();
            result.put("otpCode", userOtp);
            baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
            baseResponse.setMessage(SUCCESS_MESSAGE);
            baseResponse.setResult(result);
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    public BaseResponse verifyOtpCode(String otpCode){
        try{
            Optional<OTPEntity> isOTPExist = otpRepo.isTokenExist(otpCode);
            if(isOTPExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Invalid OTP Code");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            OTPEntity otpData = isOTPExist.get();
            String previousOtpTime =  otpData.getInsertedDt();
            LocalDateTime previousTime = LocalDateTime.parse(previousOtpTime, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
            LocalDateTime currentTime = LocalDateTime.now();
            long minutesDifference = ChronoUnit.MINUTES.between(previousTime, currentTime);
            if (minutesDifference > 7) {
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("OTP Code has expired, Kindly generate another one");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            } else {
                HashMap<String, String> otpValue = new HashMap<>();
                otpValue.put("email_address", otpData.getEmail());
                otpValue.put("customer_id", otpData.getCustomerId());
                otpValue.put("operation_type", otpData.getOperationType());

                baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
                baseResponse.setMessage("OTP Verify Successful");
                baseResponse.setResult(otpValue);
            }

        }
        catch(Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }


    public BaseResponse upgradeCustomerKyc(UpgradeKYCData upgradeKYCData){
        try{
            //Is the identityNumber exist
            Optional<CustomerIdentityRecordEntity> isExistIdentityNumber = customerIdentityRecordRepo.findByIdentityNumber(upgradeKYCData.getIdentityNumber());
            if(isExistIdentityNumber.isPresent()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Identity Number already exist ");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;

            }
            CustomerIdentityRecordEntity customerIdentityRecordEntity = new CustomerIdentityRecordEntity();
            customerIdentityRecordEntity.setCustomerId(upgradeKYCData.getCustomerId());
            customerIdentityRecordEntity.setKycId(upgradeKYCData.getIdentityId());
            customerIdentityRecordEntity.setIdentityName(upgradeKYCData.getIdentityName());
            customerIdentityRecordEntity.setIdentityNumber(upgradeKYCData.getIdentityNumber());
            customerIdentityRecordRepo.save(customerIdentityRecordEntity);

            baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
            baseResponse.setMessage("KYC Submitted successful, Await Approval");
            baseResponse.setResult(EMPTY_RESULT);
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    public BaseResponse fetchCustomerKyC(String customerId){
        try{

        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

}
