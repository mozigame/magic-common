package com.magic.api.commons.twilio;


import com.magic.api.commons.ApacheHttpClient;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.HttpResponse;
import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneNumber;

/**
 * @author jolestar Date: 10/23/13 Time: 10:04 AM
 */
public class TwilioSMSServiceProvider implements SMSServiceProvider {

    private static final String accountSid = "AC81fd25343961dd821b05ff220dc8b706";
    private static final String url = "https://api.twilio.com/2010-04-01/Accounts/%s/SMS/Messages.json";
    private static final String user = "AC81fd25343961dd821b05ff220dc8b706";
    private static final String token = "0d1f9b919063a44e0d9d302f146268f1";
    private static final String from = "+18084197888";
    private ApacheHttpClient httpClient = new ApacheHttpClient(1000, 3000);

    static class InstanceHolder {
        static TwilioSMSServiceProvider INSTANCE = new TwilioSMSServiceProvider();
    }

    public static TwilioSMSServiceProvider getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public boolean doSend(PhoneNumber phone, String msg) {
        if (phone == null)return false;
        try {
            HttpResponse response = httpClient.buildPost(String.format(url, accountSid)).withBasicAuth(user, token).withParam("From", from)
                    .withParam("To", phone.toString()).withParam("Body", msg).executeAsResponse();
            boolean result = true;
            if (response.getStatus() >= 400) {
                result = false;
                ApiLogger.error("TwilioSMSServiceProvider response error:" + response.getBodyAsString() + ",phone:" + phone.toString());
            } else {
                ApiLogger.info("TwilioSMSServiceProvider:" + response.getBodyAsString());
            }
            return result;
        } catch (ApacheHttpClient.ApiHttpClientExcpetion e) {
            ApiLogger.error("TwilioSMSServiceProvider error:" + e.getMessage() + ",phone:" + phone.toString(), e);
            return false;
        }
    }
}
