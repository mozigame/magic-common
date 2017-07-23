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
    AGENT_ADD_SUCCESS("agent_add_success"),//代理新增成功
    MEMBER_STATUS_UPDATE_SUCCESS("member_status_update_success"),//会员状态更新
    AGENT_STATUS_UPDATE_SUCCESS("agent_status_update_success"),//代理状态更新
    BC_COMPANY_STATUS_UPDATE_SUCCESS("bc_company_status_update_success"),//主控 公司账号状态更新
    BC_COMPANY_ADD_SUCCESS("bc_company_add_success"),//主控 公司账号新增成功
    BC_COMPANY_UPDATE_SUCCESS("bc_company_update_success"),//主控 公司账号修改成功
    BC_PLATFORM_LOG_ADD("bc_platform_log_add"),//主控 添加日志 --对外
    BC_PLATFORM_LOG_ADD_SUCCESS("bc_platform_log_add_success"),//主控 添加日志成功，推mongo
    BC_ACCOUNT_LOGIN_SUCCESS("bc_account_login_success"),//主控，账号登陆
    BC_ACCOUNT_LOGOUT_SUCCESS("bc_account_logout_success"),//主控，账号注销
    BC_SITE_REVIEW_SUCCESS("bc_site_review_success"), //主控站点审核成功
    MAGIC_PLATFORM_LOG_ADD("magic_platform_log_add"),//业主 添加日志 --对外
    MAGIC_PLATFORM_LOG_ADD_SUCCESS("magic_platform_log_add_success"),//业主 添加日志成功，推mongo
    MAGIC_OWNER_USER_ADD_SUCCESS("magic_owner_user_add_success"),//同步权限添加
    MAGIC_SITE_MESSAGE_ADD_SUCCESS("magic_site_message_add_success");//站内信新增
    private String value;

    Topic(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
