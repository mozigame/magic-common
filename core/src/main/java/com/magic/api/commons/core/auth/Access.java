package com.magic.api.commons.core.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义接口的访问方式
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Access {

    //验证类型
    AccessType type();

    //资源ID
    String rid() default "";

    public enum AccessType {

        /**
         * 访问类型 2无须认证 3仅内部可以访问 即IP10. 127.开头 通过intra域名访问 4指定IP访问 5拥有指定资源ID权限可访问
         */
        COMMON(1, "common"),
        PUBLIC(2, "public"),
        INTERNAL(3, "internal"),
        DESIGNATE(4, "designate"),
        RESOURCE(5, "resource"),
        COOKIE(6, "cookie");
        //TODO 添加指定IP访问验证
        private int type;
        private String name;

        AccessType(int type, String name) {
            this.type = type;
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }
}
