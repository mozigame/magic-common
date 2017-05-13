package com.magic.api.commons.mq.api;

/**
 * MQTopic
 * @author zz
 */
public enum Topic {

    PASSPORT_LOGIN_SUCCESS("passport_login_success"),
    PASSPORT_PASSWORD_RESET_SUCCESS("passport_password_reset_success"),
    PASSPORT_LOGOUT_SUCCESS("passport_logout_success"),
    USER_INFO_MODIFY_SUCCESS("user_info_modify_success"),
    MEMBER_REGISTER_SUCCESS("member_register_success"),
    MEMBER_LOGIN_SUCCESS("member_login_success"),
    MEMBER_LOGOUT_SUCCESS("member_logout_success"),
    USER_LOGIN_SUCCESS("user_login_success"),//用户登录成功
    USER_LOGOUT_SUCCESS("user_logout_success"),//用户注销成功
    AGENT_REVIEW_HISTORY("agent_review_history"),//代理审核操作历史
    AGENT_ADD_SUCCESS("agent_add_success");//代理新增成功

    private String value;

    Topic(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
