package com.macrotel.zippyworld_test.service;

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
    BankTransferTxnRepo bankTransferTxnRepo;
    @Autowired
    CableTvTxnLogRepo cableTvTxnLogRepo;
    @Autowired
    ElectricityTxnLogRepo electricityTxnLogRepo;
    @Autowired
    NetworkTxnLogRepo networkTxnLogRepo;
    @Autowired
    SqlQueries sqlQueries;

    public AppService(UtilityService utilityService) {
        this.utilityService = utilityService;
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
            messageServiceEntity.setSms("1");
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
                                        " Use it to complete registration securely. Reach out on 08039855986 for further enquiry. Thank you for joining us!";
            String smsNotificationMessage = "Welcome to Zippyworld! Your One Time PIN OTP:"+ userOtp+". Use it to complete registration securely. Reach out  on 09039855986 for further enquires";
            String smsNotification = notification.smsNotification(phoneNumber, "Zippyworld", smsNotificationMessage);
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
            //Check if user has done the same transaction before in the space of 5 minutes
            String customerId = airtimePurchaseData.getPhonenumber();
            String recipient = airtimePurchaseData.getBeneficiary_phonenumber();
            Float amount = utilities.formattedAmount(airtimePurchaseData.getAmount());
            Optional<NetworkTxnLogEntity> isTransactionExist = networkTxnLogRepo.customerRecipientLastTransaction(customerId,recipient);
            if (isTransactionExist.isPresent()){
                NetworkTxnLogEntity networkTxnLogEntity = isTransactionExist.get();
                //Compare the time to check if its less than 5 min
                LocalDateTime lastTime = LocalDateTime.parse(networkTxnLogEntity.getTimeIn(), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
                LocalDateTime currentTime = LocalDateTime.now();
                long minutesDifference = ChronoUnit.MINUTES.between(lastTime, currentTime);
                if (minutesDifference > 5) {
                    baseResponse.setStatus_code(ERROR_STATUS_CODE);
                    baseResponse.setMessage("Please wait for 3 minutes before you can recharge to "+recipient);
                    baseResponse.setResult(EMPTY_RESULT);
                    return baseResponse;
                }
            }
            //Get User Details
            Optional<UserAccountEntity> getCustomerDetail = userAccountRepo.findByPhonenumber(customerId);
            UserAccountEntity userAccountEntity =  getCustomerDetail.get();
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
            //Get Network Operator Service Code
            String networkServiceCode = airtimePurchaseData.getService_code();
            String networkOperatorCode = airtimePurchaseData.getNetwork_code();
            String txnId = customerId+"-"+airtimePurchaseData.getTransaction_id();
            String operationId = utilities.getOperationId("NU");
            List<Object[]> getNetworkOperatorServiceCode = sqlQueries.networkOperatorServiceCode(networkServiceCode,networkOperatorCode);
            if(getNetworkOperatorServiceCode.isEmpty()){
                NetworkTxnLogEntity networkTxnLogEntity = new NetworkTxnLogEntity();
                networkTxnLogEntity.setOperationId(operationId);
                networkTxnLogEntity.setTxnId(txnId);
                networkTxnLogEntity.setChannel(airtimePurchaseData.getChannel());
                networkTxnLogEntity.setUserTypeId(userTypeId);
                networkTxnLogEntity.setCustomerId(customerId);
                networkTxnLogEntity.setUserPackageId(userPackageId);
                networkTxnLogEntity.setAmount(amount);
                networkTxnLogEntity.setCommissionCharge("");
                networkTxnLogEntity.setAmountCharge("");
                networkTxnLogEntity.setRecipientNo(recipient);
                networkTxnLogEntity.setServiceAccountNo("");
                networkTxnLogEntity.setProvider("");
                networkTxnLogEntity.setRequestParam("");
                networkTxnLogEntity.setStatus("3");
                networkTxnLogEntity.setResponseComplexMessage("Invalid Network Code and Service Code");
                networkTxnLogEntity.setResponseActualMessage("Unsuccessful");
                networkTxnLogRepo.save(networkTxnLogEntity);

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


            //Check if users aggregator can get commission
            int cafValue = 1;
            double totalCommission = 0;
            double commissionAmount = 0;
            int aggregatorCommissionAmount = 0;
            if(Objects.equals(buzAggregatorCode,"BO") ||Objects.equals(buzAggregatorCode, "BM")){
                cafValue = 0;
                //Get User Commission Value
                List<Object[]> getCustomerCommission = sqlQueries.getCustomerCommissionDetail(serviceAccountNumber,userTypeId,userPackageId);
                Object[] customerCommission = getCustomerCommission.get(0);
                String cmt = customerCommission[0].toString();
                double cmp = (customerCommission[1] != null && !customerCommission[1].toString().isEmpty()) ? Double.parseDouble(customerCommission[1].toString()) : 0.0;
                double csc = (customerCommission[2] != null && !customerCommission[2].toString().isEmpty()) ? Double.parseDouble(customerCommission[2].toString()) : 0.0;
                double msv = (customerCommission[3] != null && !customerCommission[3].toString().isEmpty()) ? Double.parseDouble(customerCommission[3].toString()) : 0.0;

                if(Objects.equals(cmt,"PT")){
                    double userCommission = (amount * cmp) /100;
                    double masterCommission = (msv > 0) ? (amount * msv)/100 : msv;
                    totalCommission = userCommission + masterCommission;
                }
                else {
                    totalCommission = csc + msv;
                }

                UtilityResponse getAgentCommissionStructure =utilityService.agentCommissionStructure(totalCommission,buzAggregatorCode,customerId,userTypeId,userPackageId,serviceAccountNumber);
                if(!getAgentCommissionStructure.getStatusCode().equals(ERROR_STATUS_CODE)){
                    Map<String, Object> result = (Map<String, Object>) getAgentCommissionStructure.getResult();
                    for (Map.Entry<String, Object> entry : result.entrySet()) {
                        Map<String, Object> agentDetail = (Map<String, Object>) entry.getValue();
                        System.out.println(agentDetail);
                        String agentType = (String) agentDetail.get("agent_type");

//                        if (agentType.equals("BO")) {
//                            commissionAmount = (double) agentDetail.get("commission");
//                        } if (agentType.equals("BM")) {
//                            commissionAmount = (double) agentDetail.get("commission");
//                        }
                    }
                }


                //
            }
            else{

            }

        }
        catch (Exception ex){
            LOG.warning(ex.getMessage());
        }
        return baseResponse;
    }

}
