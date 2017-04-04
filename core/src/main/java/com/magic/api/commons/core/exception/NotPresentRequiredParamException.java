package com.magic.api.commons.core.exception;

/**
 * Created by SongJian on 2016/2/26.
 */
@SuppressWarnings("serial")
public class NotPresentRequiredParamException extends Exception {

    public NotPresentRequiredParamException() {
        super();
    }

    public NotPresentRequiredParamException(String message) {
        super(message);
    }

    public NotPresentRequiredParamException(Throwable cause) {
        super(cause);
    }

    public NotPresentRequiredParamException(String message, Throwable cause) {
        super(message, cause);
    }

}
