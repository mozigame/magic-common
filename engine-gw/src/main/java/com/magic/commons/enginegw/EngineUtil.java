/*

package com.magic.commons.enginegw;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.exception.ExceptionFactor;
import com.magic.api.commons.tools.IPUtil;
import com.magic.commons.enginegw.constants.Const;
import com.magic.commons.enginegw.util.SignUtil;
import com.magic.config.thrift.base.EGReq;
import com.magic.config.thrift.base.EGResp;
import com.magic.config.thrift.base.Trace;
import com.magic.config.thrift.uranus.EGServer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import java.util.Optional;
import java.util.Properties;

*/
/**
 * EngineUtil
 *
 * @author zj
 * @date 2017/5/8
 *//*

public class EngineUtil {

    */
/**
     * 服务器IP
     *//*

    private static final String SERVER_IP = "enginegw.thrift.server.ip";

    */
/**
     * 服务器端口
     *//*

    private static final String SERVER_PORT = "enginegw.thrift.server.port";

    */
/**
     * 连接超时时间, 毫秒
     *//*

    private static final String SERVER_TIMEOUT = "enginegw.thrift.server.timeout";

    */
/**
     * thrift 版本号
     *//*

    private static final String THRIFT_VERSION = "enginegw.thrift.server.version";


    */
/**
     * 配置文件
     *//*

    private static final String THRIFT_FILE = "thrift.properties";

    */
/**
     * Thrift配置文件
     *//*

    private static final Properties THRIFT_PROPERTIES = new Properties();

    */
/**
     * host value
     *//*

    private static final String LOAD_SERVER_IP;

    */
/**
     * port value
     *//*

    private static final int LOAD_SERVER_PORT;

    */
/**
     * timeout value
     *//*

    private static final int LOAD_SERVER_TIMEOUT;

    */
/**
     * thrift client
     *//*

    private static final EGServer.Client client;

    */
/**
     * construct method
     *//*

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

    */
/**
     * thrift server 方法入口
     * @param req 请求参数
     * @param caller 调用方
     * @return
     *//*

    public static EGResp call(EGReq req, String caller){
        return call(req, getTrace(caller));
    }

    */
/**
     * thrift server 方法入口
     * @param req 请求参数
     * @param trace 日记轨迹对象
     * @return
     *//*

    public static EGResp call(EGReq req, Trace trace){
        req.getHeader().setVersion(Const.THRIFT_VERSION);
        req.getHeader().setTimestamp(System.currentTimeMillis());
        String signature = SignUtil.sign(req);
        req.getHeader().setSignature(signature);
        EGResp resp = new EGResp();
        try {
            ApiLogger.info(String.format("call gw. req: %s, trace: %s", JSON.toJSONString(req), JSON.toJSONString(trace)));
            resp = client.CallEGService(req, trace);
            ApiLogger.info("resp:" + JSON.toJSONString(resp));
        } catch (TException e) {
            ApiLogger.error(String.format("call engine gw thrift server error. req: %s, trace: %s", JSON.toJSONString(req), JSON.toJSONString(trace)), e);
        }
        return resp;
    }


    */
/**
     * 检查server ip合法性
     *
     * @param loadServerIp
     * @return
     *//*

    private static boolean checkServerIP(String loadServerIp) {
        return Optional.ofNullable(loadServerIp).filter(ip -> loadServerIp.length() > 0).isPresent();
    }

    */
/**
     * 组装trace对象
     * @param caller 调用方系统名 -- 确保全局唯一
     * @return
     *//*

    public static Trace getTrace(String caller){
        Trace trace = new Trace();
        String logId = assembleLogId(caller);
        trace.setLogId(logId);
        trace.setCaller(caller);
        return trace;
    }

    */
/**
     * 组装LogID
     * @param caller
     * @return
     *//*

    private static String assembleLogId(String caller) {
        StringBuilder logId = new StringBuilder();
        logId.append(IPUtil.ipToInt(IPUtil.getLocalIp()));
        logId.append(Const.SPILI_CHAR);
        logId.append(caller);
        logId.append(Const.SPILI_CHAR);
        logId.append(System.currentTimeMillis());
        return logId.toString();
    }
}
*/
