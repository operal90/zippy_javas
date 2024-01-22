package com.macrotel.zippyworld_test.controller;

import com.macrotel.zippyworld_test.pojo.BaseResponse;
import com.macrotel.zippyworld_test.pojo.IdentityData;
import com.macrotel.zippyworld_test.pojo.UserCreationData;
import com.macrotel.zippyworld_test.pojo.VerifyUserIdentityData;
import com.macrotel.zippyworld_test.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    AppService appService;

    @GetMapping("/testing")
    public ResponseEntity testing(){
        BaseResponse baseResponse = appService.testing();
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/user_creation")
    public ResponseEntity userAccountCreation(@Valid @RequestBody UserCreationData userCreationData){
        BaseResponse baseResponse = appService.userCreation(userCreationData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }

    @PostMapping("/create_identity_type")
    public  ResponseEntity createIdentityType(@Valid @RequestBody IdentityData identityData){
        BaseResponse baseResponse = appService.createIdentityType(identityData);
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @GetMapping("/list_identity_type")
    public  ResponseEntity listIdentityType(){
        BaseResponse baseResponse = appService.listIdentityType();
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @GetMapping("/list_security_question")
    public  ResponseEntity listSecurityQuestion(){
        BaseResponse baseResponse = appService.listSecurityQuestion();
        HttpStatus status = (Objects.equals(baseResponse.getStatus_code(), "0") || Objects.equals(baseResponse.getStatus_code(),"1"))?HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(baseResponse,status);
    }
    @PostMapping("/verify_user_identity")
    public  ResponseEntity verifyUserIdentity(@Valid @RequestBody VerifyUserIdentityData verifyUserIdentityData){
        BaseResponse baseResponse = appService.verifyUserIdentity(verifyUserIdentityData);
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
