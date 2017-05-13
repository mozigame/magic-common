package com.magic.commons.enginegw;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.exception.ExceptionFactor;
import com.magic.api.commons.tools.IPUtil;
import com.magic.config.thrift.base.EGReq;
import com.magic.config.thrift.base.EGResp;
import com.magic.config.thrift.base.Trace;
import com.magic.config.thrift.uranus.EGServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import java.security.MessageDigest;
import java.util.Optional;
import java.util.Properties;

/**
 * EngineUtil
 *
 * @author zj
 * @date 2017/5/8
 */
public class EngineUtil {

    /**
     * 服务器IP
     */
    private static final String SERVER_IP = "enginegw.thrift.server.ip";

    /**
     * 服务器端口
     */
    private static final String SERVER_PORT = "enginegw.thrift.server.port";

    /**
     * 连接超时时间, 毫秒
     */
    private static final String SERVER_TIMEOUT = "enginegw.thrift.server.timeout";

    /**
     * thrift 版本号
     */
    private static final String THRIFT_VERSION = "enginegw.thrift.server.version";


    /**
     * 配置文件
     */
    private static final String THRIFT_FILE = "thrift.properties";

    /**
     * Thrift配置文件
     */
    private static final Properties THRIFT_PROPERTIES = new Properties();

    /**
     * host value
     */
    private static final String LOAD_SERVER_IP;

    /**
     * port value
     */
    private static final int LOAD_SERVER_PORT;

    /**
     * timeout value
     */
    private static final int LOAD_SERVER_TIMEOUT;

    /**
     * 加载thrift version
     */
    private static final byte LOAD_THRIFT_VERSION;

    /**
     * thrift client
     */
    private static final EGServer.Client client;

    /**
     * 本地ip
     */
    private static final int LOCAL_IP;

    /**
     * 分隔符
     */
    private static final String SPILI_CHAR = "-";

    /**
     * 私钥，加密
     */
    private static final String PRIVATE_KEY = "fhA4hUoBcReZ8bJddPKkqCE42sn0PzoX";

    /**
     * MD5签名分隔符
     */
    private static final String MD5_SPILT = ":";

    /**
     * construct method
     */
    public EngineUtil() {
    }

    static {
        try {
            THRIFT_PROPERTIES.load(EngineUtil.class.getClassLoader().getResourceAsStream(THRIFT_FILE));
            //服务器ip
            LOAD_SERVER_IP = THRIFT_PROPERTIES.getProperty(SERVER_IP);
            if (!checkServerIP(LOAD_SERVER_IP)){
                throw ExceptionFactor.ILLEGAL_THRIFTSERVER_IP;
            }
            //服务器port
            LOAD_SERVER_PORT = Integer.parseInt(THRIFT_PROPERTIES.getProperty(SERVER_PORT));
            //连接超时时间
            LOAD_SERVER_TIMEOUT = Integer.parseInt(THRIFT_PROPERTIES.getProperty(SERVER_TIMEOUT));
            //版本号
            LOAD_THRIFT_VERSION = Byte.parseByte(THRIFT_PROPERTIES.getProperty(THRIFT_VERSION));
            //本地IP
            LOCAL_IP = IPUtil.ipToInt(IPUtil.getLocalIp());
        }catch (Exception e){
            ApiLogger.error("load thrift file error.", e);
            throw ExceptionFactor.MISS_THRIFT_PROPERTIES_EXCEPTION;
        }
        try {
            TFramedTransport transport = new TFramedTransport(new TSocket(LOAD_SERVER_IP, LOAD_SERVER_PORT, LOAD_SERVER_TIMEOUT));
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            client = new EGServer.Client(protocol);
        }catch (TTransportException e){
            throw ExceptionFactor.INIT_ENGINEGW_CLIENT_EXCEPTION;
        }
    }


    /**
     * 组装trace对象
     * @param caller 调用方系统名 -- 确保全局唯一
     * @return
     */
    public static Trace getTrace(String caller){
        Trace trace = new Trace();
        String logId = assembleLogId(caller);
        trace.setLogId(logId);
        trace.setCaller(caller);
        return trace;
    }

    /**
     * 组装LogID
     * @param caller
     * @return
     */
    private static String assembleLogId(String caller) {
        StringBuilder logId = new StringBuilder(LOCAL_IP);
        logId.append(SPILI_CHAR);
        logId.append(caller);
        logId.append(SPILI_CHAR);
        logId.append(System.currentTimeMillis());
        return logId.toString();
    }

    /**
     * thrift server 方法入口
     * @param req 请求参数
     * @param trace 日记轨迹对象
     * @return
     */
    public static EGResp call(EGReq req, Trace trace){
        EGResp resp = new EGResp();
        try {
            req.getHeader().setVersion(LOAD_THRIFT_VERSION);
            req.getHeader().setTimestamp(System.currentTimeMillis());
            String signature = sign(req);
            req.getHeader().setSignature(signature);
            resp = client.CallEGService(req, trace);
        } catch (TException e) {
            ApiLogger.error(String.format("call engine gw thrift server error. req: %s, trace: %s", JSON.toJSONString(req), JSON.toJSONString(trace)), e);
        }
        return resp;
    }


    /**
     * thrift server 方法入口
     * @param req 请求参数
     * @param caller 调用方
     * @return
     */
    public static EGResp call(EGReq req, String caller){
        EGResp resp = new EGResp();
        try {
            req.getHeader().setVersion(LOAD_THRIFT_VERSION);
            req.getHeader().setTimestamp(System.currentTimeMillis());
            String signature = sign(req);
            req.getHeader().setSignature(signature);
            resp = client.CallEGService(req, getTrace(caller));
        } catch (TException e) {
            ApiLogger.error(String.format("call engine gw thrift server error. req: %s, caller: %s", JSON.toJSONString(req), caller), e);
        }
        return resp;
    }

    /**
     * 生成签名串
     *
     * @param req
     * @return
     */
    private static String sign(EGReq req) {
        StringBuilder reqStr = new StringBuilder();
        reqStr.append("version=");
        reqStr.append(req.getHeader().getVersion());
        reqStr.append(MD5_SPILT);
        reqStr.append("type=");
        reqStr.append(req.getHeader().getType());
        reqStr.append(MD5_SPILT);
        reqStr.append("cmd=");
        reqStr.append(req.getHeader().getTimestamp());
        reqStr.append(MD5_SPILT);
        reqStr.append("body=");
        reqStr.append(req.getBody());
        if (StringUtils.isNotEmpty(req.getHeader().getToken())){
            reqStr.append(MD5_SPILT);
            reqStr.append("token=");
            reqStr.append(req.getHeader().getToken());
        }
        String md5Str = md5(reqStr.toString());
        return md5(PRIVATE_KEY + MD5_SPILT + md5Str);
    }

    /**
     * 检查server ip合法性
     *
     * @param loadServerIp
     * @return
     */
    private static boolean checkServerIP(String loadServerIp) {
        return Optional.ofNullable(loadServerIp).filter(ip -> loadServerIp.length() > 0).isPresent();
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
            mdTemp.update(data.getBytes());
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        JSON.toJSONString(new String());
    }
}
