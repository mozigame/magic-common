package com.magic.api.commons.leju;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApacheHttpClient;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneNumber;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author jolestar Date: 11/26/13 Time: 3:38 PM
 */
public class LejuSMSServiceProvider implements SMSServiceProvider {

    private static final String service_url = "http://ems.leju.com/api/sms/send";

    private static final String key = "a48a0ea8193c4dc0ac664923c46fc3a9";
    private static final String appid = "2014061187";

    private ApacheHttpClient httpClient;
    public LejuSMSServiceProvider() {
        httpClient = new ApacheHttpClient(1000, 3000);
    }

    static LejuSMSServiceProvider smsService = new LejuSMSServiceProvider();

    public static LejuSMSServiceProvider getInstance() {
        return smsService;
    }

    @Override
    public boolean doSend(PhoneNumber phone, String msg) {
        if (phone == null)return false;
        try {
            String data = appid + phone.getNumber() + msg;
            String sign = DigestUtils.md5Hex(data + key);
            String result = httpClient.buildPost(service_url)
                    .withParam("appid", appid)
                    .withParam("mobile", phone.getNumber())
                    .withParam("content", msg)
                    .withParam("sign", sign)
                    .execute();
            ApiLogger.info("LejuSMSServiceProvider, phone:" + phone + " result:" + result);
            return this.checkResult(JSONObject.parseObject(result));
        } catch (ApacheHttpClient.ApiHttpClientExcpetion e) {
            ApiLogger.error("LejuSMSServiceProvider ", e);
            return false;
        }
    }

    private boolean checkResult(JSONObject result) {
        return result.getIntValue("status") > 0;
    }

}
