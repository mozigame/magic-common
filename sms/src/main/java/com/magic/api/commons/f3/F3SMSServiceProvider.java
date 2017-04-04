package com.magic.api.commons.f3;


import com.magic.api.commons.ApacheHttpClient;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneNumber;

import java.util.HashMap;
import java.util.Map;

/**
 * 企信通平台短信平台
 */
public class F3SMSServiceProvider implements SMSServiceProvider {

    private static String sendURL = "http://pi.noc.cn/SendSMS.aspx";
    private static String user = "305966";
    private static String password = "kuyue";

    private static F3SMSServiceProvider smsService = new F3SMSServiceProvider();

    private ApacheHttpClient httpClient;

    private F3SMSServiceProvider() {
        httpClient  = new ApacheHttpClient(1000, 3000);
    }

    public static F3SMSServiceProvider getInstance() {
        return smsService;
    }

    @Override
    public boolean doSend(PhoneNumber phone, String msg) {
        int tryNum = 0;
        while (tryNum++ < 2) {
            if (doSend_(phone, msg)) {
                return true;
            }
        }
        return false;
    }

    protected boolean doSend_(PhoneNumber phone, String msg) {
        if (phone == null) return false;
        try {
            msg = msg + "【新浪微米】";

            Map<String, String> params = new HashMap<>();
            params.put("ececcid", "" + user);
            params.put("password", password);
            params.put("msisdn", phone.getNumber() + "");
            params.put("smscontent", msg);
            params.put("msgtype", "5");
            params.put("longcode", phone.getCode() + "");
            String execute = httpClient.buildPost(sendURL).withParam(params).execute();
            ApiLogger.info("F3SMSServiceProvider code " + phone.getNumber() + " " + msg + " " + execute);

            String[] split = execute.split("\\|");
            if (split.length == 3 && "1".equals(split[0])) {
                return true;
            }

            return false;
        } catch (Exception e) {
            ApiLogger.error("F3SMSServiceProvider error " + phone.getNumber(), e);
            return false;
        }
    }
}
