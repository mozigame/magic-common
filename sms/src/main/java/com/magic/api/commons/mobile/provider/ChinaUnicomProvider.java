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
public class ChinaUnicomProvider extends AbstractChinaMobileProvider {

    /**
     * 中国联通（中国联通+中国网通) 手机号段 130、131、132、145、155、156、185、186
     */
    private static PhoneRange[] PHONE_RANGE = new PhoneRange[]{
            new PhoneRange("130"),
            new PhoneRange("131"),
            new PhoneRange("132"),
            new PhoneRange("145"),
            new PhoneRange("155"),
            new PhoneRange("156"),
            new PhoneRange("176"),
            new PhoneRange("185"),
            new PhoneRange("186")
    };

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getName()
     */
    @Override
    public String getName() {
        return "ChinaUnicom";
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
