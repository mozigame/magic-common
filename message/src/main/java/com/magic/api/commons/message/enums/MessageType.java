package com.magic.api.commons.message.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MessageType
 *
 * @author zj
 * @date 2016/8/20
 */
public enum MessageType implements Serializable{

    //消息类型 1.礼物发放 2.任务领取 3.加油棒（点赞） 4.图片发放  5.消息小助手
    giftSend(50001, "礼物发放"),
    taskNofity(50001, "任务领取"),
    fuelRod(50001, "加油棒（点赞）"),
    picSend(50001, "图片发放"),
    phpMsg(50001, "消息小助手"),
    couponSend(50001, "优惠券下发"),
    couponReward(50001, "优惠券奖励"),
    newTask(50001, "新任务通知"),
    barrageSend(50001, "弹幕发送"),
    redPacketSend(50001, "红包发送"),
    redPacketGet(50001, "红包领取"),
    fundsNoMoney(50001, "资金池没钱");

    private int value;
    private String name;

    MessageType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private static Map<Integer, MessageType> values = new HashMap<>();

    static {
        for (MessageType messageType : MessageType.values()) {
            values.put(messageType.value, messageType);
        }
    }

    public static MessageType parse(int value) {
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
