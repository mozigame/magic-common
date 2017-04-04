package com.magic.api.commons.sina;

import com.magic.api.commons.ApacheHttpClient;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneNumber;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author jolestar Date: 11/26/13 Time: 3:38 PM
 */
public class SinaSMSServiceProvider implements SMSServiceProvider {

    private static final String service_url = "http://qxt.mobile.sina.cn/cgi-bin/qxt/sendSMS.cgi";

    private static final String extendCode = "1";
    //微米长号码1069009010027
    //实惠长号码10690333660008
    private static final String longnum = "10690333660008";
    //TODO 分不同的短信用途使用不同的渠道
    //用户密码找回 97042
    //服务器监控报警 97043
    //用户注册手机绑定 97041
    //97041 微米渠道
    //86690 实惠渠道
    private static final String from = "86690";

    private ApacheHttpClient httpClient;
    public SinaSMSServiceProvider() {
        httpClient = new ApacheHttpClient(1000, 3000);
    }

    static SinaSMSServiceProvider smsService = new SinaSMSServiceProvider();

    public static SinaSMSServiceProvider getInstance() {
        return smsService;
    }

    @Override
    public boolean doSend(PhoneNumber phone, String msg) {
        if (phone == null)return false;
        try {
            String result = httpClient.buildPost(service_url).withParam("longnum", longnum).withParam("from", from)
                    .withParam("usernumber", phone.getNumber())
                    .withParam("count", 1)
                    .withParam("msg", msg)
                    .withParam("ext", extendCode)
                    .withCharset("gb2312")
                    .execute();
            ApiLogger.info("SinaSMSServiceProvider, phone:" + phone + " result:" + result);
            return this.checkResult(result);
        } catch (ApacheHttpClient.ApiHttpClientExcpetion e) {
            ApiLogger.error("SinaSMSServiceProvider ", e);
            return false;
        }
    }

    /**
     * 大于0 提交成功； -99 参数错误； -102 from值错误； -103 ip没有权限； -104 longnum错误 -105 关键字禁止；
     * -106 MD5鉴权错误
     *
     * @param result
     * @return
     */
    private boolean checkResult(String result) {
        int errorCode = NumberUtils.toInt(result, 0);
        return errorCode > 0;
    }

}
