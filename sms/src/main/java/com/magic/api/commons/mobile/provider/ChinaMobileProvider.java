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
public class ChinaMobileProvider extends AbstractChinaMobileProvider {

    /**
     * 新移动 　　 （中国移动+中国铁通）手机号段
     * 134、135、136、137、138、139、147、150、151、152、157、158、159、182、183、187、188
     */
    private static PhoneRange[] PHONE_RANGE = new PhoneRange[]{
            new PhoneRange("134"),
            new PhoneRange("135"),
            new PhoneRange("136"),
            new PhoneRange("137"),
            new PhoneRange("138"),
            new PhoneRange("139"),
            new PhoneRange("147"),
            new PhoneRange("150"),
            new PhoneRange("151"),
            new PhoneRange("152"),
            new PhoneRange("157"),
            new PhoneRange("158"),
            new PhoneRange("159"),
            new PhoneRange("178"),
            new PhoneRange("182"),
            new PhoneRange("183"),
            new PhoneRange("184"),
            new PhoneRange("187"),
            new PhoneRange("188")
    };

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getName()
     */
    @Override
    public String getName() {
        return "ChinaMobile";
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
