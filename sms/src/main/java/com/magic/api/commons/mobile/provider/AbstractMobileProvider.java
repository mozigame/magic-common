/**
 *
 */
package com.magic.api.commons.mobile.provider;


import com.magic.api.commons.mobile.MobileProvider;
import com.magic.api.commons.model.PhoneNumber;

/**
 * @author jolestar
 */
public abstract class AbstractMobileProvider implements MobileProvider {

    @Override
    public boolean isTest() {
        return false;
    }

    @Override
    public boolean isTestUid() {
        return false;
    }

    @Override
    public boolean isTestAuthCode() {
        return false;
    }

    @Override
    public boolean isSpecialPhone(PhoneNumber phone){
        return false;
    }
}
