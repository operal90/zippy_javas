package com.macrotel.zippyworld_test.pojo;

import lombok.Data;

import static com.macrotel.zippyworld_test.config.AppConstants.*;

@Data
public class BaseResponse {
    private String status_code;
    private String message;
    private Object result;

    public BaseResponse() {
    }

    public BaseResponse(boolean error) {
        this.status_code = ERROR_STATUS_CODE;
        this.message = ERROR_MESSAGE;
        this.result = EMPTY_RESULT;
    }
}
