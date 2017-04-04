package com.magic.api.commons.model;

import java.io.Serializable;

/**
 * 简单数据封装对象
 * @author zz
 */
public class SimpleResult<Z>  implements Serializable {

    private int status;

    private String msg;

    private Z data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Z getData() {
        return data;
    }

    public void setData(Z data) {
        this.data = data;
    }
}
