package com.magic.api.commons.mobile;

import com.magic.api.commons.model.PhoneNumber;

/**
 * SMSServiceProvider
 *
 * @author zj
 * @date 2016/7/21
 */
public interface SMSServiceProvider {
    /**
     * 发送短信
     *
     * @param phone
     * @param msg
     * @return
     */
    boolean doSend(PhoneNumber phone, String msg);
}
