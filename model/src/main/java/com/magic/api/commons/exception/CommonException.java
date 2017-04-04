package com.magic.api.commons.exception;

/**
 * @author zz
 */
public class CommonException extends RuntimeException {

    private int errorCode;

    public CommonException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CommonException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }


    public CommonException(String message) {
        super(message);
    }

    public CommonException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
