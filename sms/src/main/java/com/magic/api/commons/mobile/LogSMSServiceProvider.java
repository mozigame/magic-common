/**
 *
 */
package com.magic.api.commons.mobile;


import com.magic.api.commons.model.PhoneNumber;

/**
 * @author jolestar
 */
public abstract class LogSMSServiceProvider implements SMSServiceProvider {


    public static SMSServiceProvider NULL_SMS_SERVICE_PROVIDER = new LogSMSServiceProvider() {

        @Override
        public boolean doSend(PhoneNumber phone, String msg) {
            // DO nothing
            return true;
        }
    };
}
