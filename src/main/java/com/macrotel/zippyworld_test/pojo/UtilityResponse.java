package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import static com.macrotel.zippyworld_test.config.AppConstants.*;
@Data
public class UtilityResponse {
    private String statusCode;
    private String message;
    private Object result;

    public UtilityResponse() {
    }

    public UtilityResponse(boolean error) {
        this.statusCode = ERROR_STATUS_CODE;
        this.message = ERROR_MESSAGE;
        this.result = EMPTY_RESULT;
    }
}
