package com.magic.api.commons.core.exception;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.exception.CommonException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Component
public class CommonHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        HashMap<String, Object> map = new HashMap<>();
       if (CommonException.class.isAssignableFrom(e.getClass())) {
           int errorCode = ((CommonException) e).getErrorCode();
           map.put("apistatus", 1 != errorCode ? errorCode : 0);
           map.put("errorMsg", e.getMessage());
        } else {
           map.put("apistatus", 0);
           map.put("errorMsg", "对不起,服务器内部发生错误,请稍后再试.");
        }
        ApiLogger.error(e.getMessage(), e);
        FastJsonJsonView fastJsonJsonView = new FastJsonJsonView();
        fastJsonJsonView.setAttributesMap(map);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView(fastJsonJsonView);
        return modelAndView;
    }
}
