package com.magic.api.commons.message.po;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.message.enums.SideType;

/**
 * MessageUser
 *
 * @author zj
 * @date 2016/8/30
 */
public class MessageUser {

    private String id;//ID
    private String img;//头像
    private SideType type;//类型
    private String desc;//描述
    private JSONObject ext;//扩展字段


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public SideType getType() {
        return type;
    }

    public void setType(SideType type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public JSONObject getExt() {
        return ext;
    }

    public void setExt(JSONObject ext) {
        this.ext = ext;
    }
}
