package com.magic.api.commons.core;

import com.magic.api.commons.core.exception.NotPresentRequiredParamException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by SongJian on 2016/2/26.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public NotPresentRequiredParamException handleMissingServletRequestParameterException(HttpServletRequest request, Exception ex) {
        NotPresentRequiredParamException exception = new NotPresentRequiredParamException(ex.getMessage());
        return exception;
    }
}
