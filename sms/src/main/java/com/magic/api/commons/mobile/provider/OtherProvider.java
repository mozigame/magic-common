package com.magic.api.commons.mobile.provider;

import com.magic.api.commons.f3.F3SMSServiceProvider;
import com.magic.api.commons.leju.LejuSMSServiceProvider;
import com.magic.api.commons.mobile.SMSServiceProvider;
import com.magic.api.commons.model.PhoneCode;
import com.magic.api.commons.sina.SinaSMSServiceProvider;

/**
 * Created by muer on 14-10-25.
 */
public class OtherProvider extends AbstractChinaMobileProvider {

    private static PhoneRange[] PHONE_RANGE = new PhoneRange[]{

            // 虚拟运营商（电信1700，联通1709，移动1705）
            new PhoneRange("170"),

            // TODO Others
            new PhoneRange("171"),
            new PhoneRange("172"),
            new PhoneRange("173"),
            new PhoneRange("174"),
            new PhoneRange("175"),
            new PhoneRange("179")
    };

    /*
     * (non-Javadoc)
     *
     * @see me.weimi.MobileProvider#getName()
     */
    @Override
    public String getName() {
        return "OtherProvider";
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
