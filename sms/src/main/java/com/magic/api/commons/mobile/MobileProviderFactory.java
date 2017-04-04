package com.magic.api.commons.mobile;

import com.magic.api.commons.mobile.provider.*;
import com.magic.api.commons.model.PhoneNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * MobileProviderFactory
 *
 * @author zj
 * @date 2016/7/21
 */
public class MobileProviderFactory {
    static List<MobileProvider> providers = new ArrayList<MobileProvider>();

    static {
        providers.add(new ChinaMobileProvider());
        providers.add(new ChinaTelecomProvider());
        providers.add(new ChinaUnicomProvider());
        providers.add(new ForeignMobileProvider());
        providers.add(new OtherProvider());
        providers.add(new ChinaTestMobileProvider());
    }

    static MobileProviderFactory instance = new MobileProviderFactory();

    public static MobileProviderFactory getInstance() {
        return instance;
    }

    public List<MobileProvider> getMobileProviders() {
        return providers;
    }

    public MobileProvider getMobileProvider(PhoneNumber phone) {
        for (MobileProvider provider : getMobileProviders()) {
            if (provider.isSupport(phone.getCode())) {
                if (provider.isValidPhone(phone.getNumber())) {
                    return provider;
                }
            }
        }
        return MobileProvider.NULL;
    }
}
