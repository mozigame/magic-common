package com.magic.api.commons.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApacheHttpClient;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.message.constants.Const;
import com.magic.api.commons.message.enums.ConvType;
import com.magic.api.commons.message.enums.MessageType;
import com.magic.api.commons.message.po.Message;
import com.magic.api.commons.message.po.MessageUser;
import java.util.Map;

/**
 * MessageUtil
 *
 * @author zj
 * @date 2016/8/20
 */
public class MessageUtil {

    private static ApacheHttpClient httpClient = new ApacheHttpClient(1000,3000);

    /**
     * 消息发送
     * @param messageType   消息类型
     * @param fromUser    发送方
     * @param toUser  接收方
     * @param ext  消息数据
     * @return
     */
    public static boolean sendMsg(MessageType messageType, MessageUser fromUser, MessageUser toUser, JSONObject ext){
        ApiLogger.info("send message enter. msgType:" + messageType + ",from:" + JSON.toJSONString(fromUser) + ",to:" + JSON.toJSONString(toUser) + ",ext:" + ext.toJSONString());
        return sendMsg(messageType, fromUser, toUser, ext, null);
    }

    /**
     * push离线消息
     *
     * @param userId    接收用户id
     * @param content   离线消息内容
     * @param data  附带数据  如跳转链接等
     * @return
     */
    public static boolean sendOffLineMsg(long userId, String content, Map data){
        ApiLogger.info("push message enter. userId:" + userId + ",content:" + content + ",data:" + JSON.toJSONString(data));
        try {
            String executeAsyncString = httpClient.buildPost(Const.SEND_OFFLINE_URL)
                    .withParam("userId", userId).withParam("content", content)
                    .withParam("data", JSON.toJSONString(data)).executeAsyncString();
            JSONObject result = JSON.parseObject(executeAsyncString);
            if(null != result){
                String code = result.getString("code");
                if("200".equals(code)){
                    ApiLogger.info("push message success. userId:" + userId + ",content:" + content);
                    return true;
                }else{
                    ApiLogger.info("push message fail. userId:" + userId + ",content:" + content + ",code:" + code);
                    return false;
                }
            }else{
                ApiLogger.info("push message fail. userId:" + userId + ",content:" + content + ",result:" + executeAsyncString);
                return false;
            }
        }catch (Exception e){
            ApiLogger.error("push message. userId:" + userId + ",content:" + content, e);
        }
        return false;
    }


    /**
     * 消息发送
     * @param messageType   消息类型
     * @param fromUser    发送方
     * @param toUser  接收方
     * @param ext  消息数据
     * @return
     */
    public static boolean sendMsg(MessageType messageType, MessageUser fromUser, MessageUser toUser, JSONObject ext, String desc){
        ApiLogger.info("send message enter. msgType:" + messageType + ",from:" + JSON.toJSONString(fromUser) + ",to:" + JSON.toJSONString(toUser) + ",ext:" + JSON.toJSONString(ext) + ",desc:" + desc);
        try {
            Message message = new Message();
            message.setFrom(fromUser);
            message.setTo(toUser);
            message.setExt(ext);
            JSONObject data = new JSONObject();
            data.put("data", message);
            data.put("type", messageType);
            if (desc != null) data.put("desc", desc);
            String executeAsyncString = httpClient.buildPost(Const.SEND_NOTICE_URL)
                    .withParam("fromuid", fromUser.getId())
                    .withParam("touid", toUser.getId())
                    .withParam("dataid", messageType.getValue())
                    .withParam("convtype", convType(messageType).name())
                    .withParam("data", JSON.toJSONString(data))
                    .executeAsyncString();
            JSONObject result = JSON.parseObject(executeAsyncString);
            if(null != result){
                String code = result.getString("code");
                if("200".equals(code)){
                    ApiLogger.info("send msg success. msgType:" + messageType + ",from:" + JSON.toJSONString(fromUser) + ",to:" + JSON.toJSONString(toUser));
                    return true;
                }else{
                    ApiLogger.info("send msg fail. msgType:" + messageType + ",from:" + JSON.toJSONString(fromUser) + ",to:" + JSON.toJSONString(toUser) + ",code:" + code);
                    return false;
                }
            }else{
                ApiLogger.info("send msg fail. msgType:" + messageType + ",from:" + JSON.toJSONString(fromUser) + ",to:" + JSON.toJSONString(toUser) + ",result:" + executeAsyncString);
                return false;
            }
        }catch (Exception e){
            ApiLogger.error("send msg error. msgType:" + messageType + ",from:" + JSON.toJSONString(fromUser) + ",to:" + JSON.toJSONString(toUser), e);
        }

        return false;
    }

    /**
     * 根据消息类型返回convType
     * @param type
     * @return
     */
    private static ConvType convType(MessageType type){
        ConvType convType = null;
        switch (type){
            case giftSend:
                convType = ConvType.room;
                break;
            case taskNofity:
                convType = ConvType.single;
                break;
            case fuelRod:
                convType = ConvType.room;
                break;
            case picSend:
                convType = ConvType.room;
                break;
            case phpMsg:
                convType = ConvType.single;
                break;
            case couponSend:
                convType = ConvType.room;
                break;
            case barrageSend:
                convType = ConvType.room;
                break;
            case newTask:
                convType = ConvType.single;
                break;
            case redPacketGet:
                convType = ConvType.room;
                break;
            case redPacketSend:
                convType = ConvType.room;
                break;
            case fundsNoMoney:
                convType = ConvType.room;
                break;
            default:
                convType = ConvType.single;
                break;
        }
        return convType;
    }

}
