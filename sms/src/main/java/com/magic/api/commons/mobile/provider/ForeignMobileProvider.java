/**
 *
 */
package com.magic.api.commons.mobile.provider;


import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneCode;
import com.magic.api.commons.twilio.TwilioSMSServiceProvider;

/**
 * @author jolestar
 */
public class ForeignMobileProvider extends AbstractMobileProvider {

    // 最小5位 最大11位
    private static final long MIN_PHONE = 10000;
    private static final long MAX_PHONE = 99999999999L;

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getName()
     */
    @Override
    public String getName() {
        return "ForeignMobile";
    }

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#isSupport(int)
     */
    @Override
    public boolean isSupport(int phoneCode) {
        // 支持除了中国大陆以及测试区域码外的所有区域码
        if (phoneCode == PhoneCode.CHINA_PHONE_CODE || phoneCode == PhoneCode.TEST_PHONE_CODE) {
            return false;
        }
        for (PhoneCode code : PhoneCode.getPhoneCodes()) {
            if (code.getCode() == phoneCode) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#isValidPhone(long)
     */
    @Override
    public boolean isValidPhone(long phone) {
        return phone > MIN_PHONE && phone < MAX_PHONE;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getSmsServiceProviders()
     */
    @Override
    public SMSServiceProvider[] getSmsServiceProviders(int type) {
        return new SMSServiceProvider[]{TwilioSMSServiceProvider.getInstance()};
    }

}
