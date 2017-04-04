/**
 *
 */
package com.magic.api.commons.mobile.provider;

/**
 * @author jolestar
 */
public abstract class AbstractChinaMobileProvider extends AbstractMobileProvider {

    public boolean inRange(long phoneNum) {
        PhoneRange[] ranges = this.getPhoneNumRange();
        for (PhoneRange range : ranges) {
            if (range.inRange(phoneNum)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidPhone(long phone) {
        return this.inRange(phone);
    }

    public abstract PhoneRange[] getPhoneNumRange();

}
