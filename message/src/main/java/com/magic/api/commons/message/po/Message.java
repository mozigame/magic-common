package com.magic.api.commons.message.po;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Message
 *
 * @author zj
 * @date 2016/8/22
 */
public class Message implements Serializable{
    //消息发送方
    private MessageUser from;
    //消息接收方
    private MessageUser to;
    //扩展字段
    private JSONObject ext;

    public MessageUser getFrom() {
        return from;
    }

    public void setFrom(MessageUser from) {
        this.from = from;
    }

    public MessageUser getTo() {
        return to;
    }

    public void setTo(MessageUser to) {
        this.to = to;
    }

    public JSONObject getExt() {
        return ext;
    }

    public void setExt(JSONObject ext) {
        this.ext = ext;
    }
}
