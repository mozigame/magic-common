package com.magic.api.commons.model;

import java.io.Serializable;

/**
 * 简单数据封装对象
 * @author zz
 */
public class SimpleListResult<Z>  implements Serializable {

    private Integer status;

    private String msg;

    private Z list;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Z getList() {
        return list;
    }

    public void setList(Z list) {
        this.list = list;
    }
}
