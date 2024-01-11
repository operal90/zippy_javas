package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.entity.*;
import com.macrotel.zippyworld_test.pojo.BaseResponse;
import com.macrotel.zippyworld_test.pojo.UserCreationData;
import com.macrotel.zippyworld_test.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.logging.Logger;
import com.macrotel.Utilities;

import static com.macrotel.zippyworld_test.config.AppConstants.*;
@Service
public class AppService {

    BaseResponse baseResponse = new BaseResponse(true);
    Utilities utilities = new Utilities();
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
            //Check if BVN already exist
            Optional<UserAccountEntity> isBVNExist = userAccountRepo.findByBvn(userCreationData.getBvn());
            if(isBVNExist.isPresent()){
                baseResponse.setStatus_code(ERROR_STATUS_CODE);
                baseResponse.setMessage("BVN already registered.");
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
            //Convert User FirstName and Lastname to generate Account Name, get Transaction Id, Reference Id
            String accountName = userCreationData.getFirstname() +' '+ userCreationData.getLastname();
            String transactionId = userCreationData.getPhonenumber()+'-'+userCreationData.getTransaction_id();
            String referenceId = utilities.randomAlphanumeric(15);
            //Insert into User Account Table
            UserAccountEntity userAccountEntity = new UserAccountEntity();
            userAccountEntity.setFirstname(userCreationData.getFirstname());
            userAccountEntity.setLastname(userCreationData.getLastname());
            userAccountEntity.setPhonenumber(userCreationData.getPhonenumber());
            userAccountEntity.setAccountNo("");
            userAccountEntity.setPromoCode(userCreationData.getPromo_code());
            userAccountEntity.setReferrerCode(userCreationData.getReferrer_code());
            userAccountEntity.setReferralPhonenumber(userCreationData.getReferral_phonenumber());
            userAccountEntity.setBvn(userCreationData.getBvn());
            userAccountEntity.setBvnPhonenumber("");
            userAccountEntity.setStatus("0");
            userAccountEntity.setWtlStatus("2");
            userAccountEntity.setKycLevel("");
            userAccountEntity.setParentAggregatorCode(userCreationData.getAggregator_code());
            userAccountEntity.setGender(userCreationData.getGender());
            userAccountEntity.setEmail(userCreationData.getEmail());
            userAccountEntity.setPin(utilities.encryptPassword(userCreationData.getPin()));
            userAccountEntity.setTxnPin("001122");
            userAccountEntity.setSecureId(userCreationData.getSecure_id());
            userAccountEntity.setAnswer(userCreationData.getAnswer());
            userAccountEntity.setUserType("");
            userAccountEntity.setUserPackageId("");
            userAccountEntity.setAgreed("");
            userAccountEntity.setCommissionMode("INSTANCE");
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
}
