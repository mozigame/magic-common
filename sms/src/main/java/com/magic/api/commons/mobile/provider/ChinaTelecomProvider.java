/**
 *
 */
package com.magic.api.commons.mobile.provider;

import com.magic.api.commons.f3.F3SMSServiceProvider;
import com.magic.api.commons.leju.LejuSMSServiceProvider;
import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneCode;
import com.magic.api.commons.sina.SinaSMSServiceProvider;

/**
 * @author jolestar
 */
public class ChinaTelecomProvider extends AbstractChinaMobileProvider {

    /**
     * 新电信 　　 （中国电信 <http://baike.baidu.com/view/3214.htm>+中国卫通）手机号码开头数字
     * 133、153、189、180、181
     */
    private static PhoneRange[] PHONE_RANGE = new PhoneRange[]{
            new PhoneRange("133"),
            new PhoneRange("153"),
            new PhoneRange("177"),
            new PhoneRange("189"),
            new PhoneRange("180"),
            new PhoneRange("181")
    };

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getName()
     */
    @Override
    public String getName() {
        return "ChinaTelecom";
    }

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getPhoneNumRange()
     */
    @Override
    public PhoneRange[] getPhoneNumRange() {
        return PHONE_RANGE;
    }

    @Override
    public SMSServiceProvider[] getSmsServiceProviders(int type) {
        if (type == 1) {
            return new SMSServiceProvider[]{F3SMSServiceProvider.getInstance()};
        } else {
            return new SMSServiceProvider[]{SinaSMSServiceProvider.getInstance(), LejuSMSServiceProvider.getInstance()};
        }
    }

    @Override
    public boolean isSupport(int phoneCode) {
        return phoneCode == PhoneCode.CHINA_PHONE_CODE;
    }
}
