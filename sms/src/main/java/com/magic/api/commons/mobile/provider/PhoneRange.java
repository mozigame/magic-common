package com.magic.api.commons.mobile.provider;


import org.apache.commons.lang3.math.NumberUtils;

public class PhoneRange {
    public final long start;
    public final long end;

    public final String prefix;

    public PhoneRange(String prefix) {
        if (prefix.length() != 3) {
            throw new IllegalArgumentException("invalid phone prefix " + prefix);
        }
        start = NumberUtils.toLong(prefix + "00000000");
        end = NumberUtils.toLong(prefix + "99999999");
        this.prefix = prefix;
    }

    public boolean inRange(long value) {
        return value >= start && value <= end;
    }
}
