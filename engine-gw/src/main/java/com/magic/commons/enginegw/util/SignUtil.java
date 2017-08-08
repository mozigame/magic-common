package com.magic.commons.enginegw.util;

import com.magic.api.commons.ApiLogger;
import com.magic.commons.enginegw.constants.Const;
import com.magic.config.thrift.base.EGHeader;
import com.magic.config.thrift.base.EGReq;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * SignUtil
 *
 * @author zj
 * @date 2017/5/15
 */
public class SignUtil {

    /**
     * 生成签名串 sign
     *
     * @param req
     * @return
     */
    public static String sign(EGReq req) {
        StringBuilder reqStr = new StringBuilder();
        EGHeader header = req.getHeader();
        reqStr.append("version=");
        reqStr.append(header.getVersion());
        reqStr.append(Const.MD5_SPILT);
        reqStr.append("type=");
        reqStr.append(header.getType().getValue());
        reqStr.append(Const.MD5_SPILT);
        reqStr.append("cmd=");
        reqStr.append("0x");
        reqStr.append(Long.toHexString(header.getCmd()));
        reqStr.append(Const.MD5_SPILT);
        reqStr.append("timestamp=");
        reqStr.append(header.getTimestamp());
        reqStr.append(Const.MD5_SPILT);
        reqStr.append("body=");
        reqStr.append(req.getBody());
        if (StringUtils.isNotEmpty(header.getToken())){
            reqStr.append(Const.MD5_SPILT);
            reqStr.append("token=");
            reqStr.append(header.getToken());
        }
        String md5Str = md5(reqStr.toString());
        ApiLogger.info(String.format("before sign. str:%s, sign: %s", reqStr.toString(), md5Str));
        return md5(Const.PRIVATE_KEY + Const.MD5_SPILT + md5Str).toUpperCase();
    }

    /**
     * 签名
     *
     * @param data
     * @return
     */
    public final static String md5(String data) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(data.getBytes("utf-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            ApiLogger.info("char array." + new String(str));
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        char[] str = new char[]{'a','b'};
        System.out.println(str);
        String body = "中国";
        System.out.println(md5(body));
        String req = "version=1:type=80:cmd=5242891:timestamp=1497869712022:body={\"incomeFeeRate\":15,\"withdrawFeeRate\":10,\"withdrawFeeMax\":99990000,\"ownerId\":10001,\"incomeFeeMax\":300000,\"poundageName\":\"shouxufeifangan007\"}";
        System.out.println(md5(req));

        EGHeader header = new EGHeader();
        header.setCmd(0x1004);
        System.out.println(header.cmd);
        System.out.println(Long.toHexString(header.cmd));
    }

}
