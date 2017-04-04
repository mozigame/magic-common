/**
 *
 */
package com.magic.api.commons;

import com.magic.api.commons.mobile.MobileProvider;
import com.magic.api.commons.mobile.MobileProviderFactory;
import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneCode;
import com.magic.api.commons.model.PhoneNumber;
import com.magic.api.commons.thread.StandardThreadExecutor;

import java.util.concurrent.Callable;

/**
 * 发送短信的工具类
 *
 * @author jolestar
 */
public class SMSSender {


    public SMSSender() {
    }

    private static SMSSender sender = new SMSSender();

    private StandardThreadExecutor executor = new StandardThreadExecutor();

    public static SMSSender getInstance() {
        return sender;
    }

    /**
     * 不支持多国手机号，所以废弃
     *
     * @param phone
     * @param msg
     * @return
     */
    @Deprecated
    public boolean send(long phone, String msg, int type) {
        return this.send(new PhoneNumber(phone), msg, type);
    }

    public boolean send(PhoneNumber phone, String msg, int type) {
        return this.doSend(phone, msg, 0, type);
    }

    public boolean send(PhoneNumber phone, String msg,long hash, int type) {
        return this.doSend(phone, msg, hash, type);
    }

    private boolean doSend(final PhoneNumber phone, final String msg, final long hash, int type) {
        if (phone == null)return false;
        MobileProvider provider = this.getMobileProvider(phone);
        final SMSServiceProvider[] smsServices = provider.getSmsServiceProviders(type);
        if (smsServices == null || smsServices.length == 0) {
            ApiLogger.info("invalid phone number,can not find SmsServiceProvider by phone:" + phone);
            return false;
        }
        this.executor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (smsServices.length == 1) {
                    return smsServices[0].doSend(phone, msg);
                }
                for (int i = 0; i < smsServices.length; i++) {
                    int index = (int) ((hash + i) % smsServices.length);
                    boolean result = smsServices[index].doSend(phone, msg);
                    if (result) {
                        return true;
                    }
                }
                return false;
            }
        });
        return true;
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @param code
     * @param hash  hash决定了使用短信服务的顺序
     * @return
     */
    public boolean sendVerifyCode(long phone, String code, long hash, int type) {
        return this.doSend(new PhoneNumber(PhoneCode.CHINA_PHONE_CODE, phone), getVerifyCodeMsg(code ,hash), hash, type);
    }

    public boolean sendVerifyCode(PhoneNumber phone, String code, long hash, int type) {
        return this.doSend(phone, getVerifyCodeMsg(code ,hash), hash, type);
    }

    protected MobileProvider getMobileProvider(PhoneNumber phone) {
        return MobileProviderFactory.getInstance().getMobileProvider(phone);
    }

    private String getVerifyCodeMsg(String code, long hash) {
        String msg = "验证码：%s。30分钟内有效。如非本人操作，请致电4006-611-388。";
        if(hash % 2 == 0){
            msg = "【直播】" + msg;
        }
        return String.format(msg, code);
    }
}
