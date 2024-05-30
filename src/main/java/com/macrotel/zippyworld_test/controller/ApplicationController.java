package com.macrotel.zippyworld_test.controller;

import com.macrotel.zippyworld_test.pojo.*;
import com.macrotel.zippyworld_test.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

@RestController
@RequestMapping("/zippyworld_test")
public class ApplicationController {

    private AppService appService;
    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/testing")
    public ResponseEntity<BaseResponse> testing(){
        BaseResponse baseResponse = appService.testing();
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/user_creation")
    public ResponseEntity<BaseResponse> userAccountCreation(@Valid @RequestBody UserCreationData userCreationData){
        BaseResponse baseResponse = appService.userCreation(userCreationData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }

    @PostMapping("/create_identity_type")
    public  ResponseEntity<BaseResponse> createIdentityType(@Valid @RequestBody IdentityData identityData){
        BaseResponse baseResponse = appService.createIdentityType(identityData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @GetMapping("/list_identity_type")
    public  ResponseEntity<BaseResponse> listIdentityType(){
        BaseResponse baseResponse = appService.listIdentityType();
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @GetMapping("/list_security_question")
    public  ResponseEntity<BaseResponse> listSecurityQuestion(){
        BaseResponse baseResponse = appService.listSecurityQuestion();
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/verify_user_identity")
    public  ResponseEntity<BaseResponse> verifyUserIdentity(@Valid @RequestBody VerifyUserIdentityData verifyUserIdentityData){
        BaseResponse baseResponse = appService.verifyUserIdentity(verifyUserIdentityData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }

    @PostMapping("/generate_registration_otp")
    public ResponseEntity<BaseResponse> generateRegistrationOtp(@Valid @RequestBody NotificationData notificationData){
        BaseResponse baseResponse = appService.generateRegistrationOTPCode(notificationData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @GetMapping("/verify_otp")
    public ResponseEntity<BaseResponse> verifyOtp(@RequestParam("otp_code") String otpCode){
        BaseResponse baseResponse = appService.verifyOtpCode(otpCode);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @GetMapping("/list_pending_customer_kyc")
    public ResponseEntity<BaseResponse> listPendingCustomerKyc(){
        BaseResponse baseResponse = appService.listPendingCustomerKYC();
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/submit_customer_kyc")
    public ResponseEntity<BaseResponse> submitCustomerKyc(@Valid @RequestBody SubmitKYCData upgradeKYCData){
        BaseResponse baseResponse = appService.submitCustomerKyc(upgradeKYCData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }

    @GetMapping("/fetch_customer_kyc")
    public ResponseEntity<BaseResponse> fetchUserKyc(@RequestParam("customerId") String customerId){
        BaseResponse baseResponse = appService.fetchCustomerKyC(customerId);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/upgrade_customer_kyc")
    public ResponseEntity<BaseResponse> upgradeCustomerKyc(@Valid @RequestBody UpgradeKYCData upgradeKYCData){
        BaseResponse baseResponse = appService.upgradeCustomerKyc(upgradeKYCData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }

    @PostMapping("/airtime_purchase")
    public ResponseEntity<BaseResponse> airtimePurchase(@Valid @RequestBody AirtimePurchaseData airtimePurchaseData){
        BaseResponse baseResponse = appService.airtimePurchase(airtimePurchaseData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/electricity_vending")
    public ResponseEntity<BaseResponse> electricityVending(@Valid @RequestBody ElectricityData electricityData){
        BaseResponse baseResponse = appService.electricityVending(electricityData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/login")
    public ResponseEntity<BaseResponse> userLogin(@Valid @RequestBody LoginData loginData){
        BaseResponse baseResponse = appService.userLogin(loginData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @GetMapping("/get_data_plans")
    public ResponseEntity<BaseResponse> getDataPlan(@Param("service_code") String service_code, @Param("network_code") String network_code, @Param("channel") String channel){
        BaseResponse baseResponse = appService.getDataPlans(service_code,network_code,channel);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }

    @PostMapping("/data_purchase")
    public ResponseEntity<BaseResponse> dataPurchase(@Valid @RequestBody DataPurchaseData dataPurchaseData){
        BaseResponse baseResponse = appService.dataPurchase(dataPurchaseData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/query_customer_wallet_balance")
    public ResponseEntity<BaseResponse> queryCustomerWalletBalance(@Valid @RequestBody CustomerQueryData customerQueryData){
        BaseResponse baseResponse = appService.queryCustomerWalletBalance(customerQueryData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/query_customer_commission_wallet_balance")
    public ResponseEntity<BaseResponse> queryCustomerCommissionWalletBalance(@Valid @RequestBody CustomerQueryData customerQueryData){
        BaseResponse baseResponse = appService.queryCustomerCommissionWalletBalance(customerQueryData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/commission_earned")
    public ResponseEntity<BaseResponse> commissionEarned(@Valid @RequestBody CommissionEarnedData commissionEarnedData){
        BaseResponse baseResponse = appService.commissionEarned(commissionEarnedData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }

    @GetMapping("/getgardenscustomerdetails")
    public ResponseEntity<BaseResponse> getGardensCustomerDetails(@Param("meternumber") String meternumber){
        BaseResponse baseResponse = appService.getGardensCustomerDetails(meternumber);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus_code(ERROR_STATUS_CODE);
        baseResponse.setMessage("An error occurred");
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        baseResponse.setResult(errors);
        return baseResponse;

    }
}
