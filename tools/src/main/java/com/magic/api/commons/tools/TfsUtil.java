package com.magic.api.commons.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApacheHttpClient;
import com.magic.api.commons.ApiHttpClient;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.ByteFileUpload;
import org.apache.commons.lang3.StringUtils;

/**
 * TfsUtil TFS工具
 *
 * @author zj
 * @date 2015/12/18
 */
public class TfsUtil {

    private static ApacheHttpClient httpClient = new ApacheHttpClient(3000, 60000);

    private static String TFS_UPLOAD_URL;

    static {
        switch (EnvUtil.getEnv()) {
            case prod:
                TFS_UPLOAD_URL = "http://img.intra.hiwemeet.com/weiphoto/upload_file";
                break;
            default:
                TFS_UPLOAD_URL = "http://10.0.8.12:2080/weiphoto/upload_file";
                break;
        }
    }

    /**
     * 上传文件流至TFS服务器
     *
     * @param bytes
     * @return 保存的文件id
     */
    public static String uploadFile(byte[] bytes) {
        return uploadFile(null, bytes);
    }

    /**
     * 上传文件流至TFS服务器
     *
     * @param fileName      文件名称
     * @param bytes         上传文件数据
     * @return 保存的文件id
     */
    public static String uploadFile(String fileName, byte[] bytes) {
        JSONObject result = null;
        String executeAsyncString;
        try {
            ApiHttpClient.RequestBuilder requestBuilder = httpClient.buildPost(TFS_UPLOAD_URL);
            if (StringUtils.isBlank(fileName)) {
                executeAsyncString = requestBuilder.withParam("file", bytes).executeAsyncString();
            } else {
                executeAsyncString = requestBuilder.withParam("file", new ByteFileUpload(fileName, bytes)).executeAsyncString();
            }
            result = JSON.parseObject(executeAsyncString);
            String fileid = result.getString("fileid");
            if (null == fileid) {
                ApiLogger.info("保存订单明细表至TFS失败！data size:".concat(String.valueOf(bytes.length)).concat(",tfsUrl:").concat(TFS_UPLOAD_URL));
                return null;
            }
            ApiLogger.info("返回信息: ".concat(result.toJSONString()));
            return fileid;
        } catch (Exception e) {
            ApiLogger.error("上传文件至TFS出错!".concat(JSON.toJSONString(result)));
            return null;
        }
    }

}
