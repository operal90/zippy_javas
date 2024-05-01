package com.macrotel.zippyworld_test.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macrotel.zippyworld_test.config.Notification;
import com.macrotel.zippyworld_test.repo.SqlQueries;
import com.macrotel.zippyworld_test.config.ThirdPartyAPI;
import com.macrotel.zippyworld_test.config.UtilityConfiguration;
import com.macrotel.zippyworld_test.dto.IdentityTypeDTO;
import com.macrotel.zippyworld_test.dto.KycDTO;
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

   private final UtilityService utilityService;
   private final LoggingService loggingService;

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
    @Autowired
    VerificationRepo verificationRepo;

    @Autowired
    NetworkTxnLogRepo networkTxnLogRepo;
    @Autowired
    SqlQueries sqlQueries;

    public AppService(UtilityService utilityService, LoggingService loggingService) {
        this.utilityService = utilityService;
        this.loggingService = loggingService;
    }

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
            //Check if Identity Number already exist if Register without BVN. Registration without BVN is 0001
            if(!Objects.equals(userCreationData.getIdentityNumber(), "0001")){
                Optional<UserAccountEntity> isIdentityNumberExist = userAccountRepo.findByIdentityNumber(userCreationData.getIdentityNumber());
                if (isIdentityNumberExist.isPresent()) {
                    baseResponse.setStatus_code(ERROR_STATUS_CODE);
                    baseResponse.setMessage("Identity Number already registered.");
                    baseResponse.setResult(EMPTY_RESULT);
                    return baseResponse;
                }
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
            String referenceId = utilities.referenceId();

            //Generate Account Number
            String identityUrl = "https://vps.providusbank.com/vps/api/PiPCreateReservedAccountNumber";
            Map<String, String> headers = new HashMap<>();
            headers.put("Client-Id", "bUBjUjBUM0xfUHIpKCkuTTEyMw==");
            headers.put("X-Auth-Signature", " 9d5f1854bc0ebb9efa378354a5468ee156ae03c5265687c95cf9173d8eb62c1beb4ca616c40c35a55dd38a9a93415c3c98999f9d67020a1bc278bd3db23f26fc");
            headers.put("Content-Type", " application/json");
            HashMap<String, String> formParams = new HashMap<>();
            formParams.put("account_name", accountName);
            formParams.put("bvn", userCreationData.getIdentityNumber());
            Object generateAccountNumber = thirdPartyAPI.callAPI(identityUrl, HttpMethod.POST,headers,formParams);

            String accountNumber = userCreationData.getPhonenumber();
            String userAccountName = accountName;
            Map<String, Object> apiResponse = (Map<String, Object>) generateAccountNumber;
            if(generateAccountNumber != null){
                String responseCode = (String) apiResponse.get("responseCode");
                if(Objects.equals(responseCode, "00")) {
                    accountNumber = (String) apiResponse.get("account_number");
                    userAccountName = (String) apiResponse.get("account_name");
                }
            }
            String kycLevel = "1";
            if(Objects.equals(userCreationData.getIdentityNumber(),"0001")){
                kycLevel = "0";
            }
            //Check if the user email is the same with the email gotten from the identity verification
            String userIdentityEmail = "";
            Optional<VerificationEntity> getUserEmail = verificationRepo.getUserVerificationData(userCreationData.getPhonenumber());
            if(getUserEmail.isPresent()){
                VerificationEntity verificationEntity = getUserEmail.get();
                userIdentityEmail =  verificationEntity.getIdentityEmail();
            }
            if(!Objects.equals(userIdentityEmail, userCreationData.getEmail())){
                kycLevel = "0";
            }

            //Insert into User Account Table
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setFirstname(userCreationData.getFirstname());
            userAccountEntity.setLastname(userCreationData.getLastname());
            userAccountEntity.setPhonenumber(userCreationData.getPhonenumber());
            userAccountEntity.setAccountNo(accountNumber);
            userAccountEntity.setAccountName(userAccountName);
            userAccountEntity.setGender(userCreationData.getGender());
            userAccountEntity.setPromoCode(userCreationData.getPromo_code());
            userAccountEntity.setReferrerCode(userCreationData.getReferrer_code());
            userAccountEntity.setReferralPhonenumber(userCreationData.getReferral_phonenumber());
            userAccountEntity.setIdentityId(userCreationData.getIdentityId());
            userAccountEntity.setIdentityNumber(userCreationData.getIdentityNumber());
            userAccountEntity.setStatus("0");
            userAccountEntity.setWtlStatus("2");
            userAccountEntity.setKycLevel(kycLevel);
            userAccountEntity.setParentAggregatorCode(userCreationData.getAggregator_code());
            userAccountEntity.setGender(userCreationData.getGender());
            userAccountEntity.setEmail(userCreationData.getEmail());
            userAccountEntity.setPin(utilities.shaEncryption(userCreationData.getPin()));
            userAccountEntity.setTxnPin(utilities.shaEncryption("001122"));
            userAccountEntity.setSecureId(userCreationData.getSecure_id());
            userAccountEntity.setAnswer(utilities.shaEncryption(userCreationData.getAnswer()));
            userAccountEntity.setUserType("1");
            userAccountEntity.setUserPackageId("1");
            userAccountEntity.setPndStatus("1");
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
            messageServiceEntity.setSms("0");
            messageServiceEntity.setWhatsapp("1");
            messageServiceRepo.save(messageServiceEntity);

            //Insert into Customer Identity Record
            CustomerIdentityRecordEntity customerIdentityRecord = new CustomerIdentityRecordEntity();
            customerIdentityRecord.setIdentityName(userCreationData.getFirstname()+" "+userCreationData.getLastname());
            customerIdentityRecord.setCustomerId(userCreationData.getPhonenumber());
            customerIdentityRecord.setKycId(userCreationData.getIdentityId());
            customerIdentityRecord.setIdentityNumber(userCreationData.getIdentityNumber());
            customerIdentityRecord.setStatus("0");
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
            String emailAddress ="";
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
                    return baseResponse;
                }
                else{
                    Map<String, Object> apiResponse = (Map<String, Object>) getUserBVNData;
                    String statusCode = (String) apiResponse.get("status_code");
                    if(Objects.equals(statusCode, "1")){
                        baseResponse.setStatus_code(ERROR_STATUS_CODE);
                        baseResponse.setMessage("No result found for this NIN");
                        baseResponse.setResult(EMPTY_RESULT);
                        return baseResponse;
                    }
                    else{
                        Map<String, Object> apiResult = (Map<String, Object>) apiResponse.get("result");
                          if(apiResult != null){
                              if (apiResult.containsKey("personal_info") && apiResult.get("personal_info") != null) {
                                  Map<String, Object> personalInfo = (Map<String, Object>) apiResult.get("personal_info");
                                  if (personalInfo.containsKey("email") && personalInfo.get("email") != null) {
                                      emailAddress = (String) personalInfo.get("email");
                                  }
                              }
                          }
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
                    return baseResponse;
                }
                else{
                    Map<String, Object> apiResponse = (Map<String, Object>) getUserBVNData;
                    String statusCode = (String) apiResponse.get("status_code");
                    if(Objects.equals(statusCode, "1")){
                        baseResponse.setStatus_code(ERROR_STATUS_CODE);
                        baseResponse.setMessage("No result found for this BVN");
                        baseResponse.setResult(EMPTY_RESULT);
                        return baseResponse;
                    }
                    else{
                        Map<String, Object> apiResult = (Map<String, Object>) apiResponse.get("result");
                        if (apiResult.containsKey("personal_info") && apiResult.get("personal_info") != null) {
                            Map<String, Object> personalInfo = (Map<String, Object>) apiResult.get("personal_info");
                            if (personalInfo.containsKey("email") && personalInfo.get("email") != null) {
                                emailAddress = (String) personalInfo.get("email");
                            }
                        }
                        baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
                        baseResponse.setMessage(SUCCESS_MESSAGE);
                        baseResponse.setResult(apiResponse.get("result"));
                    }
                }
            }
            emailAddress = emailAddress.toLowerCase();
            VerificationEntity verificationEntity = new VerificationEntity();
            verificationEntity.setIdentityId(identityId);
            verificationEntity.setIdentityEmail(emailAddress);
            verificationEntity.setIdentityNumber(verifyUserIdentityData.getIdentityNumber());
            verificationEntity.setCustomerId(verifyUserIdentityData.getPhoneNumber());
            verificationRepo.save(verificationEntity);

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
            String userOtp = utilities.randomDigit(7);
            OTPEntity otpEntity = new OTPEntity();
            otpEntity.setToken1(userOtp);
            otpEntity.setToken(utilities.shaEncryption(userOtp));
            otpEntity.setCustomerId(notificationData.getPhoneNumber());
            otpEntity.setEmail(notificationData.getEmailAddress());
            otpEntity.setOperationType("REGISTRATION");
            otpRepo.save(otpEntity);

            String notificationMessage = "Welcome to Zippyworld! " +
                                        "Your One-Time Passcode (OTP) is: "+userOtp+"." +
                                        " Use it to complete registration securely. Reach out on 08039855986 for further enquiry. Thank you for joining us!";
            String smsNotificationMessage = "Welcome to Zippyworld! Your OTP:"+ userOtp+". Use it to complete registration securely.";
            notification.smsNotification(phoneNumber, "Zippyworld", smsNotificationMessage);
            notification.emailNotification(emailAddress,username,"Zippyworld", notificationMessage);
            notification.whatsappNotification(phoneNumber, "Zippyworld", notificationMessage);
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

    public BaseResponse submitCustomerKyc(SubmitKYCData upgradeKYCData){
        try{
            //Is customer Exist
            Optional<UserAccountEntity> isCustomerExist = userAccountRepo.findByPhonenumber(upgradeKYCData.getCustomerId());
            if(isCustomerExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Invalid Customer Id, Kindly Create Account");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
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
            customerIdentityRecordEntity.setStatus("1");
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
            //Is customer Exist
            Optional<UserAccountEntity> isCustomerExist = userAccountRepo.findByPhonenumber(customerId);
            if(isCustomerExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Invalid Customer Id, Kindly Create Account");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Get customer data from database
            List<CustomerIdentityRecordEntity> getCustomerKYCRecord = customerIdentityRecordRepo.findByCustomerId(customerId);
            if(getCustomerKYCRecord.isEmpty()){
                baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
                baseResponse.setMessage(SUCCESS_MESSAGE);
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            ArrayList<Object> result = new ArrayList<>();
            for(CustomerIdentityRecordEntity customerKyCData : getCustomerKYCRecord){
                Long kycId = Long.parseLong(customerKyCData.getKycId());
                Optional<IdentityEntity> getIdentity = identityRepo.findById(kycId);
                IdentityEntity identityEntity = getIdentity.get();
                KycDTO kycDTO = new KycDTO();
                kycDTO.setCustomerId(customerKyCData.getCustomerId());
                kycDTO.setCustomerName(customerKyCData.getIdentityName());
                kycDTO.setIdentityType(identityEntity.getIdentityType());
                kycDTO.setIdentityNumber(customerKyCData.getIdentityNumber());
                kycDTO.setDateCreated(customerKyCData.getInsertedDt());
                kycDTO.setStatus((customerKyCData.getStatus().equals("0") ?"Approved" :"Pending Approval"));
                result.add(kycDTO);
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

    public BaseResponse upgradeCustomerKyc(UpgradeKYCData upgradeKYCData){
        try{
            //Get the customer kyc from database
            Optional<CustomerIdentityRecordEntity> isIdentityExist = customerIdentityRecordRepo.findByIdentityNumber(upgradeKYCData.getIdentityNumber());
            if(isIdentityExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Identity Number does not exist");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

            CustomerIdentityRecordEntity customerIdentityRecordEntity = isIdentityExist.get();
            //Check if the identity is previously approved;
            if(Objects.equals("0", customerIdentityRecordEntity.getStatus())){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Identity has been previously approved");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Approval Status 0 for Accepted and 1 for Rejected;
            String approvalStatus = upgradeKYCData.getApproval();
            String approvalMessage = "";
            String customerId = customerIdentityRecordEntity.getCustomerId();
            if(Objects.equals("0", approvalStatus)){
                //Check if user have active KYC
                String kycLevel = "1";
                List<CustomerIdentityRecordEntity> getCustomerKYCRecord = customerIdentityRecordRepo.customerActiveKyc(customerId);
                if(!getCustomerKYCRecord.isEmpty()){
                    kycLevel = "2";
                }
                //Get user data and Update User KC Level in the account table;
                Optional<UserAccountEntity> getUserData = userAccountRepo.findByPhonenumber(customerId);
                if(getUserData.isPresent()){
                    UserAccountEntity userAccountEntity = getUserData.get();
                    userAccountEntity.setKycLevel(kycLevel);
                    userAccountRepo.save(userAccountEntity);
                }
                //Update KYC Status to approved and save
                approvalMessage ="Identity Approved Successful, Customer KYC is now at Level"+kycLevel;
                customerIdentityRecordEntity.setStatus("0");
                customerIdentityRecordRepo.save(customerIdentityRecordEntity);
            }
            else{
            //Delete the customer Identity number
                approvalMessage ="Identity Rejected Successful";
                customerIdentityRecordRepo.delete(customerIdentityRecordEntity);
            }

            baseResponse.setStatus_code(SUCCESS_STATUS_CODE);
            baseResponse.setMessage(approvalMessage);
            baseResponse.setResult(EMPTY_RESULT);
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    //Utilities
    public BaseResponse airtimePurchase(AirtimePurchaseData airtimePurchaseData){
        try{
            //Get necessary data needed
            String securityAnswer = utilities.shaEncryption(airtimePurchaseData.getSecurity_answer());
            String customerId = airtimePurchaseData.getPhonenumber();
            String recipient = airtimePurchaseData.getBeneficiary_phonenumber();
            double amount = utilities.formattedAmount(airtimePurchaseData.getAmount());
            String channel = airtimePurchaseData.getChannel();
            //Check if user account exist
            Optional<UserAccountEntity> isCustomerExist = userAccountRepo.findByPhonenumber(customerId);
            if(isCustomerExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Customer Account does not exit");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Confirm Security answer
            boolean confirmSecurityAnswer = utilityService.confirmSecurityAnswer(customerId,securityAnswer);
            if(!confirmSecurityAnswer){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Incorrect Security Answer");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

            //Check if user session token
            int sessionToken = utilityService.checkSessionToken(customerId,airtimePurchaseData.getToken());
            if(sessionToken != 0){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Session Expired, Kindly Relogin");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

            //Check User have sufficient balance
            Object getCustomerWalletBalance = utilityService.queryCustomerWalletBalance(customerId);
            Map<String, String> customerWalletBalance = (Map<String, String>) getCustomerWalletBalance;
            double customerWalletBalanceAmount = Double.parseDouble(customerWalletBalance.get("amount"));
            if(amount > customerWalletBalanceAmount){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Insufficient Wallet Balance");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Get User Details and check user kyc level
            UserAccountEntity userAccountEntity =  isCustomerExist.get();
            String userKycLevel = userAccountEntity.getKycLevel();
            String customerName= userAccountEntity.getFirstname() +" "+userAccountEntity.getLastname();
            String customerEmail = userAccountEntity.getEmail();
            String userTypeId = userAccountEntity.getUserType();
            String userPackageId = userAccountEntity.getUserPackageId();
            String parentAggregatorCode = userAccountEntity.getParentAggregatorCode();
            String buzAggregatorCode = userAccountEntity.getParentAggregatorCode().toUpperCase().substring(0,2);
            String commissionMode = userAccountEntity.getCommissionMode();
            String pndStatus = userAccountEntity.getPndStatus();

            //Check if User is on Post No Debit
            if(!Objects.equals("1", pndStatus)){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("The account is on Post No Debit, kindly contact the  customer service.");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Check User KYC Level
            if(Objects.equals(userKycLevel,"0")){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("You can not perform this transaction due to your KYC. Kindly upgrade your KYC or contact the customer support.");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Check if user is on KYC Level 9
            if(Objects.equals(userKycLevel,"9")){
                if(amount > 5000){
                    baseResponse.setStatus_code(ERROR_STATUS_CODE);
                    baseResponse.setMessage("You can not perform this transaction due to your KYC limit. Kindly upgrade your KYC to perform transaction above N5,000 or contact the customer support");
                    baseResponse.setResult(EMPTY_RESULT);
                    return baseResponse;
                }
            }

            //Check if user has done the same transaction before in the space of 5 minutes
            Optional<NetworkTxnLogEntity> isTransactionExist = networkTxnLogRepo.customerRecipientLastTransaction(customerId,recipient);
            if (isTransactionExist.isPresent()){
                NetworkTxnLogEntity networkTxnLogEntity = isTransactionExist.get();
                //Compare the time to check if it is less than 5 min
                LocalDateTime lastTime = LocalDateTime.parse(networkTxnLogEntity.getTimeIn(), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
                LocalDateTime currentTime = LocalDateTime.now();

                long minutesDifference = ChronoUnit.MINUTES.between(lastTime, currentTime);
                if (minutesDifference < 3) {
                    baseResponse.setStatus_code(ERROR_STATUS_CODE);
                    baseResponse.setMessage("Please wait for 3 minutes before you can recharge to "+recipient);
                    baseResponse.setResult(EMPTY_RESULT);
                    return baseResponse;
                }
            }

            //Get Network Operator Service Code
            String networkServiceCode = airtimePurchaseData.getService_code();
            String networkOperatorCode = airtimePurchaseData.getNetwork_code();
//            String txnId = customerId+"-"+airtimePurchaseData.getTransaction_id();
            String txnId = customerId+"-"+utilities.randomDigit(9);
            String operationId = utilities.getOperationId("NU");
            List<Object[]> getNetworkOperatorServiceCode = sqlQueries.networkOperatorServiceCode(networkServiceCode,networkOperatorCode);
            if(getNetworkOperatorServiceCode.isEmpty()){
                //Save the Network log
                loggingService.networkRequestLog(operationId,txnId,airtimePurchaseData.getChannel(),userTypeId,customerId,userPackageId,amount,
                        0,0,recipient,"","","",
                        "3","Invalid Network Code and Service Code","Unsuccessful");

                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Please select the Network");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

            //Network Provider Details
            Object[] networkOperatorServiceCode =getNetworkOperatorServiceCode.get(0);
            String provider = networkOperatorServiceCode[4].toString();
            String network = networkOperatorServiceCode[2].toString();
            String serviceAccountNumber = networkOperatorServiceCode[0].toString();
            String serviceCommissionAccountNumber = networkOperatorServiceCode[1].toString();
            String operationCode = networkOperatorServiceCode[3].toString();
            String description = networkOperatorServiceCode[5].toString();


            //Check if users aggregator can get commission
            int cafValue = 1;
            double totalCommission = 0;
            double commissionAmount = 0;
            double aggregatorCommissionAmount = 0;
            if(Objects.equals(buzAggregatorCode,"BO") ||Objects.equals(buzAggregatorCode, "BM")){
                cafValue = 0;
                //Get User Commission Value
                Object getServiceCommission =  utilityService.getServiceCommission2(amount,serviceAccountNumber,userTypeId,userPackageId);
                Map<String, Double> servicecommissionMap = (Map<String, Double>) getServiceCommission;
                double commissionMaster = servicecommissionMap.get("commissionMaster");
                double commissionUser = servicecommissionMap.get("commissionUser");
                totalCommission =  commissionUser + commissionMaster;


                UtilityResponse getAgentCommissionStructure =utilityService.agentCommissionStructure(totalCommission,buzAggregatorCode,customerId,userTypeId,userPackageId,serviceAccountNumber);
                if(!getAgentCommissionStructure.getStatusCode().equals(ERROR_STATUS_CODE)){
                    Map<String, Object> result = (Map<String, Object>) getAgentCommissionStructure.getResult();
                    for (Map.Entry<String, Object> entry : result.entrySet()) {
                        Map<String, Object> agentDetail = (Map<String, Object>) entry.getValue();
                        String agentType = (String) agentDetail.get("agentType");
                        if (agentType.equals("BO")) {
                            commissionAmount = Double.parseDouble((String) agentDetail.get("commission"));
                        }else if (agentType.equals("BM")) {
                            commissionAmount = Double.parseDouble((String) agentDetail.get("commission"));
                        }
                    }
                    //Ask hm to clearify commission amount
                }

            }
            else{
                Object getPromoServiceCommission =  utilityService.getPromoServiceCommission(userTypeId,userPackageId,customerId,serviceAccountNumber,amount,parentAggregatorCode);
                Map<String, Double> commissionMap = (Map<String, Double>) getPromoServiceCommission;
                double commissionMaster = commissionMap.get("commissionMaster");
                double commissionUser = commissionMap.get("commissionUser");
                if(!Objects.equals(commissionMaster,0.0) && !Objects.equals(commissionUser,0.0)){
                    commissionAmount = commissionUser;
                    aggregatorCommissionAmount = commissionMaster;
                }
                else {
                    Object getServiceCommission =  utilityService.getServiceCommission2(amount,serviceAccountNumber,userTypeId,userPackageId);
                    Map<String, Double> servicecommissionMap = (Map<String, Double>) getServiceCommission;
                    System.out.println(servicecommissionMap);
                    commissionAmount = servicecommissionMap.get("commissionUser");
                    aggregatorCommissionAmount =servicecommissionMap.get("commissionMaster");
                }
                totalCommission = commissionAmount + aggregatorCommissionAmount;
                //check if user's aggregator can get commission
                cafValue = utilityService.checkAggregatorFund(customerId);
            }
            double totalCharge = utilities.formattedAmount(String.valueOf(amount-totalCommission));
            String operationSummary = description + " of " + totalCharge;
            //Save the Network log and get the Id
            Long responseId = loggingService.networkRequestLog(operationId,txnId,airtimePurchaseData.getChannel(),userTypeId,customerId,userPackageId,amount,
                    totalCommission,totalCharge,recipient,serviceAccountNumber,provider,"","","","");
            if(responseId > 0){
                //CheckSessionToken\

                if(sessionToken == 0 || Objects.equals(channel,"SMART-KEYPAD-POS") || Objects.equals(channel, "GRAVITY-POS")){
                    try{
                        //Connect to the airtimePurchase utility
                        Object airtimePurchaseUtility = utilityService.airtimePurchase(operationId,customerId,customerName,customerEmail,userTypeId,userPackageId,commissionMode,recipient,amount,
                                commissionAmount,totalCharge,channel,serviceAccountNumber,serviceCommissionAccountNumber,networkOperatorCode,networkServiceCode,provider,network,operationCode,operationSummary);

                        Map<String, String> getAirtimePurchaseResult = (Map<String, String>) airtimePurchaseUtility;
                        String airtimePurchaseStatusCode = getAirtimePurchaseResult.get("statusCode");
                        String airtimePurchaseStatusMessage = getAirtimePurchaseResult.get("statusMessage");
                        String airtimePurchaseMessage = getAirtimePurchaseResult.get("message");
                        //Logging the transaction
                        loggingService.responseTxnLogging("AIRTIME-PURCHASE",String.valueOf(responseId),airtimePurchaseMessage,airtimePurchaseStatusCode,airtimePurchaseStatusMessage);
                        //Update Transaction
                        sqlQueries.updateTransactionStatus(customerId,operationId,airtimePurchaseStatusMessage);

                        if(!Objects.equals(airtimePurchaseStatusCode,"1") && !Objects.equals(cafValue,"1")){
                            Object getSettingValue = utilityService.getSettingValue("DEFAULT_AGGREGATOR_CODE");
                            Map<String, String> settingValueMap = (Map<String, String>) getSettingValue;
                            String settingValueResult = (String) settingValueMap.get("result");
                            if((aggregatorCommissionAmount > 0) && (!Objects.equals(parentAggregatorCode,settingValueResult))){

                            } else if (Objects.equals(buzAggregatorCode,"BO") ||Objects.equals(buzAggregatorCode,"BM")){

                            }
                        }
                        baseResponse.setStatus_code(airtimePurchaseStatusCode);
                        baseResponse.setMessage(airtimePurchaseMessage);
                        baseResponse.setResult(getAirtimePurchaseResult);

                    }
                    catch (Exception ex){
                        //Update Transaction
                        double newFormattedAmount = utilities.twoDecimalFormattedAmount(String.valueOf(amount));
                        sqlQueries.updateTransactionStatus(customerId,operationId,"Pending");
                        baseResponse.setStatus_code(ERROR_STATUS_CODE);
                        baseResponse.setMessage("Your airtime recharge of N"+newFormattedAmount+" is pending/successful, Confirm the status from customer service. Thank you for using Zippyworld");
                        baseResponse.setResult(EMPTY_RESULT);
                        return baseResponse;
                    }
                }
                else{
                    loggingService.responseTxnLogging("airtime-purchase",String.valueOf(responseId),"Session Expired, Kindly relogin","5","Unsuccessful");
                    baseResponse.setStatus_code("5");
                    baseResponse.setMessage("Session Expired, Kindly Relogin");
                    baseResponse.setResult(EMPTY_RESULT);
                    return baseResponse;
                }
            }
            else{
                String newTransactionId = txnId+"-DT-"+utilities.randomDigit(10);
                loggingService.networkRequestLog(operationId,newTransactionId,channel,userTypeId,customerId,userPackageId,amount,commissionAmount,
                        totalCharge,recipient,serviceAccountNumber,network,"","3",String.valueOf(responseId), "Unsuccessful");
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage(String.valueOf(responseId));
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    public BaseResponse electricityVending(ElectricityData electricityData){
        try{
            //Get necessary data needed
            String securityAnswer = utilities.shaEncryption(electricityData.getSecurity_answer());
            String customerId = electricityData.getPhonenumber();
            String token = electricityData.getToken();
            String cardIdentity = electricityData.getCard_identity();
            double amount = utilities.formattedAmount(electricityData.getAmount());
            String buyerPhoneNumber = electricityData.getBuyer_phonenumber();
            String buyerEmailAddress = electricityData.getBuyer_email();
            String providerRef = electricityData.getProvider_ref();
            String buyerName = electricityData.getCustomer_name();
            String customerAddress = electricityData.getCustomer_address();
            String channel = electricityData.getChannel();
            String operatorId = electricityData.getOperator_id();
            String accountTypeId = electricityData.getAccount_type_id();
            String provider = "SHAGO";

            //Check if Customer Account exist
            Optional<UserAccountEntity> isCustomerExist = userAccountRepo.findByPhonenumber(customerId);
            if(isCustomerExist.isEmpty()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Customer Account does not exit");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

            //Confirm Security answer
            boolean confirmSecurityAnswer = utilityService.confirmSecurityAnswer(customerId,securityAnswer);
            if(!confirmSecurityAnswer){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Incorrect Security Answer");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

            //Check if user session token
            int sessionToken = utilityService.checkSessionToken(customerId,token);
            if(sessionToken != 0){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Session Expired, Kindly Relogin");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

            //Check User have sufficient balance
            Object getCustomerWalletBalance = utilityService.queryCustomerWalletBalance(customerId);
            Map<String, String> customerWalletBalance = (Map<String, String>) getCustomerWalletBalance;
            double customerWalletBalanceAmount = Double.parseDouble(customerWalletBalance.get("amount"));
            if(amount > customerWalletBalanceAmount){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Insufficient Wallet Balance");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Get User Details
            UserAccountEntity userAccountEntity =  isCustomerExist.get();
            String customerName= userAccountEntity.getFirstname() +" "+userAccountEntity.getLastname();
            String customerEmail = userAccountEntity.getEmail();
            String userTypeId = userAccountEntity.getUserType();
            String userPackageId = userAccountEntity.getUserPackageId();
            String parentAggregatorCode = userAccountEntity.getParentAggregatorCode();
            String buzAggregatorCode = userAccountEntity.getParentAggregatorCode().toUpperCase().substring(0,2);
            String commissionMode = userAccountEntity.getCommissionMode();
            String pndStatus = userAccountEntity.getPndStatus();
            String userKycLevel = userAccountEntity.getKycLevel();

            //Check User KYC Level
            if(Objects.equals(userKycLevel,"0")){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("You can not perform this transaction due to your KYC. Kindly upgrade your KYC or contact the customer support.");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
            //Check if user is on KYC Level 9
            if(Objects.equals(userKycLevel,"9")){
                if(amount > 5000){
                    baseResponse.setStatus_code(ERROR_STATUS_CODE);
                    baseResponse.setMessage("You can not perform this transaction due to your KYC limit. Kindly upgrade your KYC to perform transaction above N5,000 or contact the customer support");
                    baseResponse.setResult(EMPTY_RESULT);
                    return baseResponse;
                }
            }

            //Check if User is on Post No Debit
            if(!Objects.equals("1", pndStatus)){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("The account is on Post No Debit, kindly contact the  customer service.");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }

            //Check previous transaction
            int checkPreviousStatus = utilityService.checkPreviousTxnStatus(customerId, cardIdentity, ELECTRICITY_SERVICE_CODE);
            if(checkPreviousStatus !=1){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("Please wait for 3 minutes before you can vend electricity to "+cardIdentity);
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }


            String serviceAccountNo = "1000000012";
            String serviceCommissionAccountNo = "1000000061";
            //Check if users aggregator can get commission
            int cafValue = 1;
            double totalCommission = 0;
            double commissionAmount = 0;
            double aggregatorCommissionAmount = 0;
            if(Objects.equals(buzAggregatorCode,"BO") ||Objects.equals(buzAggregatorCode, "BM")){
                cafValue = 0;
                //Get User Commission Value
                Object getServiceCommission =  utilityService.getServiceCommission2(amount,serviceAccountNo,userTypeId,userPackageId);
                Map<String, Double> servicecommissionMap = (Map<String, Double>) getServiceCommission;
                double commissionMaster = servicecommissionMap.get("commissionMaster");
                double commissionUser = servicecommissionMap.get("commissionUser");
                totalCommission =  commissionUser + commissionMaster;


                UtilityResponse getAgentCommissionStructure =utilityService.agentCommissionStructure(totalCommission,buzAggregatorCode,customerId,userTypeId,userPackageId,serviceAccountNo);
                if(!getAgentCommissionStructure.getStatusCode().equals(ERROR_STATUS_CODE)){
                    Map<String, Object> result = (Map<String, Object>) getAgentCommissionStructure.getResult();
                    for (Map.Entry<String, Object> entry : result.entrySet()) {
                        Map<String, Object> agentDetail = (Map<String, Object>) entry.getValue();
                        String agentType = (String) agentDetail.get("agentType");
                        if (agentType.equals("BO")) {
                            commissionAmount = Double.parseDouble((String) agentDetail.get("commission"));
                        }else if (agentType.equals("BM")) {
                            commissionAmount = Double.parseDouble((String) agentDetail.get("commission"));
                        }
                    }
                }

            }
            else{
                Object getPromoServiceCommission =  utilityService.getPromoServiceCommission(userTypeId,userPackageId,customerId,serviceAccountNo,amount,parentAggregatorCode);
                Map<String, Double> commissionMap = (Map<String, Double>) getPromoServiceCommission;
                double commissionMaster = commissionMap.get("commissionMaster");
                double commissionUser = commissionMap.get("commissionUser");
                if(!Objects.equals(commissionMaster,0.0) && !Objects.equals(commissionUser,0.0)){
                    commissionAmount = commissionUser;
                    aggregatorCommissionAmount = commissionMaster;
                }
                else {
                    Object getServiceCommission =  utilityService.getServiceCommission2(amount,serviceAccountNo,userTypeId,userPackageId);
                    Map<String, Double> servicecommissionMap = (Map<String, Double>) getServiceCommission;
                    commissionAmount = servicecommissionMap.get("commissionUser");
                    aggregatorCommissionAmount =servicecommissionMap.get("commissionMaster");
                }
                totalCommission = commissionAmount + aggregatorCommissionAmount;
                //check if user's aggregator can get commission
                cafValue = utilityService.checkAggregatorFund(customerId);
            }

            double totalCharge = utilities.formattedAmount(String.valueOf(amount-totalCommission));
            String txnId = customerId+"-"+utilities.randomDigit(9);
            String operationId = utilities.getOperationId("NU");
            String params = buyerPhoneNumber+"|"+buyerEmailAddress+"|"+providerRef+"|"+buyerName+"|"+customerAddress;

            //Log the transaction in the electricity request logging and get id;
            Long responseId  = loggingService.electricityRequestLogging(operationId,txnId,channel,userTypeId,customerId,userPackageId,buyerName,customerAddress,amount,totalCommission,
                                                    totalCharge,cardIdentity,operatorId,accountTypeId,provider,params,"","","");

            if(responseId > 0) {
                //Check session Token
                if(sessionToken == 0 || Objects.equals(channel,"SMART-KEYPAD-POS") || Objects.equals(channel, "GRAVITY-POS")){
                    try{
                        Object electricityPurchaseUtility = utilityService.electricityPurchase(operationId,customerId,customerName,customerEmail,userTypeId,userPackageId,commissionMode,
                                cardIdentity,serviceAccountNo,serviceCommissionAccountNo,amount,commissionAmount,totalCharge,channel,accountTypeId,operatorId,providerRef,buyerPhoneNumber,
                                buyerEmailAddress,provider,buyerName,customerAddress,"");

                        Map<String, String> getElectricityPurchaseResult = (Map<String, String>) electricityPurchaseUtility;
                        String electricityPurchaseStatusCode = getElectricityPurchaseResult.get("statusCode");
                        String electricityPurchaseStatusMessage = getElectricityPurchaseResult.get("statusMessage");
                        String electricityPurchaseMessage = getElectricityPurchaseResult.get("message");
                        String electricityToken =  getElectricityPurchaseResult.get("token");
                        String responseMessage = new ObjectMapper().writeValueAsString(getElectricityPurchaseResult);
                        //Logging the transaction
                        loggingService.electricityRequestUpdate(electricityToken, String.valueOf(responseId),responseMessage,electricityPurchaseStatusCode,electricityPurchaseMessage);
                        //Update Transaction
                        sqlQueries.updateTransactionStatus(customerId,operationId,electricityPurchaseStatusMessage);

                        if(!Objects.equals(electricityPurchaseStatusCode,"1") && !Objects.equals(cafValue,"1")){
                            Object getSettingValue = utilityService.getSettingValue("DEFAULT_AGGREGATOR_CODE");
                            Map<String, String> settingValueMap = (Map<String, String>) getSettingValue;
                            String settingValueResult = (String) settingValueMap.get("result");
                            if((aggregatorCommissionAmount > 0) && (!Objects.equals(parentAggregatorCode,settingValueResult))){

                            } else if (Objects.equals(buzAggregatorCode,"BO") ||Objects.equals(buzAggregatorCode,"BM")){

                            }
                        }
                        getElectricityPurchaseResult.remove("messageDetails");
                        baseResponse.setStatus_code(electricityPurchaseStatusCode);
                        baseResponse.setMessage(electricityPurchaseMessage);
                        baseResponse.setResult(getElectricityPurchaseResult);
                    }
                    catch (Exception ex){
                        double newFormattedAmount = utilities.twoDecimalFormattedAmount(String.valueOf(amount));
                        sqlQueries.updateTransactionStatus(customerId,operationId,"Pending");
                        baseResponse.setStatus_code(ERROR_STATUS_CODE);
                        baseResponse.setMessage("Your Electricity vending of N"+newFormattedAmount+" is pending/successful, Confirm the status from customer service. Thank you for using Zippyworld");
                        baseResponse.setResult(EMPTY_RESULT);
                        return baseResponse;
                    }
                }
                else{
                    loggingService.electricityRequestUpdate("",String.valueOf(responseId),"Session Expired, Kindly Relogin","5","Unsuccessful");
                    baseResponse.setStatus_code("5");
                    baseResponse.setMessage("Session Expired, Kindly Relogin");
                    baseResponse.setResult(EMPTY_RESULT);
                    return baseResponse;
                }
            }
            else{
                String newTransactionId = txnId+"-DT-"+utilities.randomDigit(10);
                loggingService.electricityRequestLogging(operationId,txnId,channel,userTypeId,customerId,userPackageId,buyerName,customerAddress,amount,totalCommission,
                        totalCharge,cardIdentity,operatorId,accountTypeId,provider,params,"3",String.valueOf(responseId),"Unsuccessful");
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage(String.valueOf(responseId));
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

    public BaseResponse userLogin(LoginData loginData){
        try{
            //Check if phoneNumber is unique;
            boolean isPhoneNumberUnique = utilityService.isPhoneNumberUnique(loginData.getPhonenumber());
            if(!isPhoneNumberUnique){
                baseResponse.setStatus_code("1");
                baseResponse.setMessage("Phone number does not exist. Kindly sign up");
                baseResponse.setResult(EMPTY_RESULT);
                return baseResponse;
            }
        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

}
