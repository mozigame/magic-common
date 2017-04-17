package com.magic.api.commons.core.exception;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.log.RequestLogRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * 异常统一处理
 * @author zz
 */
@Component("handlerExceptionResolver")
public class CommonHandlerExceptionResolver implements HandlerExceptionResolver {

    /**
     * 异常处理
     * @param httpServletRequest        HttpServletRequest
     * @param httpServletResponse       HttpServletResponse
     * @param o                         发生异常对象
     * @param e                         Exception
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        HashMap<String, Object> map = new HashMap<>();
        if (e instanceof CommonException) {
            CommonException commonException = (CommonException) e;
            assembleError(httpServletResponse, commonException.getEnMessage(), commonException.getCnMessage(), commonException.getErrorCode(), commonException.getHttpCode(), map);
        } else if (e instanceof ServletRequestBindingException) {
            assembleError(httpServletResponse, e.getMessage(), e.getMessage(), ExceptionFactor.DEFAULT_EXCEPTION.getErrorCode(), HttpServletResponse.SC_BAD_REQUEST, map);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            assembleError(httpServletResponse, e.getMessage(), e.getMessage(), ExceptionFactor.DEFAULT_EXCEPTION.getErrorCode(), HttpServletResponse.SC_METHOD_NOT_ALLOWED, map);
        } else {
            assembleError(httpServletResponse, ExceptionFactor.DEFAULT_EXCEPTION.getEnMessage(), ExceptionFactor.DEFAULT_EXCEPTION.getCnMessage()
                    , ExceptionFactor.DEFAULT_EXCEPTION.getErrorCode(), ExceptionFactor.DEFAULT_EXCEPTION.getHttpCode(), map);
        }
        ApiLogger.error(e.getMessage(), e);
        FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
        fastJsonJsonView.setAttributesMap(map);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(fastJsonJsonView);
        return modelAndView;
    }

    /**
     * 封装并记录错误信息
     * @param httpServletResponse   HttpServletResponse
     * @param enErrorMsg            enErrorMsg
     * @param cnErrorMsg            cnErrorMsg
     * @param errorCode             errorCode
     * @param httpCode              httpCode
     * @param map                   Json
     */
    private void assembleError(HttpServletResponse httpServletResponse,String enErrorMsg, String cnErrorMsg, long errorCode, int httpCode, HashMap<String, Object> map) {
        map.put("apistatus", 0);
        map.put("errorMsg", cnErrorMsg);
        map.put("errorMsgEn", enErrorMsg);
        map.put("errorCode", errorCode);
        RequestLogRecord requestLogRecord = RequestContext.getRequestContext().getRequestLogRecord();
        requestLogRecord.setResponse(map);
        requestLogRecord.setResponseStatus(httpCode);
        httpServletResponse.setStatus(httpCode);
    }
}
