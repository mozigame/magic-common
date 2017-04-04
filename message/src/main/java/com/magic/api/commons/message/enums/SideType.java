package com.magic.api.commons.message.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * SideType
 *
 * @author zj
 * @date 2016/8/30
 */
public enum SideType {

    //消息类型 1.用户 2.组
    user(50001, "用户"),
    group(50001, "组");

    private int value;
    private String name;

    SideType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private static Map<Integer, SideType> values = new HashMap<>();

    static {
        for (SideType sideType : SideType.values()) {
            values.put(sideType.value, sideType);
        }
    }

    public static SideType parse(int value) {
        return values.get(value);
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
