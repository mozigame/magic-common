package com.magic.api.commons.mq.api;

/**
 * MQTopic
 * @author zz
 */
public enum Topic {

    TEST_TOPIC("zb_test_topic"),

    USER_REGISTER_SUCCESS("zb_user_register_success"),
    USER_LOGIN_SUCCESS("zb_user_login_success"),
    USER_UPDATE_SUCCESS("zb_user_update_success"),
    LEVEL_IMPROVE_PERSON_INFO("zb_user_level_improve_info"),
    LEVEL_BOUND_PHONE("zb_user_level_bound_phone"),
    LEVEL_LOGIN_EVERYDAY("zb_user_level_login_everyday"),
    LEVEL_SHARE_LIVE("zb_user_level_share_live"),
    LEVEL_STAY_TIME("zb_user_level_stay_time"),
    LEVEL_ATTENTION_SENDMSG("zb_user_level_attention_send"),
    LEVEL_SPEND_BEAN("zb_user_level_spend_bean"),

    ORDER_CREATE("zb_order_create"),
    SEND_GIFT("zb_send_gift"),

    FUELROD_SEND_SUCCESS("zb_fuelrod_send_success"),
    LIVE_START("zb_live_start"),
    UPDATE_LIVE_STATUS("zb_update_live_status"),
    LIVE_FINISHED("zb_live_finished"),
    UPDATE_LIVE_HASVIDEO("zb_update_live_hasvideo"),

    TASK_RECEIVE("zb_task_receive"),
    TASK_REJECT("zb_task_reject");

    private String value;

    Topic(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
