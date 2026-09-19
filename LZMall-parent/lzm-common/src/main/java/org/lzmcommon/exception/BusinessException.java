package org.lzmcommon.exception;

import lombok.Getter;
import org.lzmcommon.result.ResultCode;

@Getter
public class BusinessException extends RuntimeException {

    private ResultCode code;
    private String message;


    public BusinessException(ResultCode code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_SERVER_ERROR;
        this.message = message;
    }

}