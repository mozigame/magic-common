package com.magic.commons.enginegw.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.protocol.TProtocol;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.tools.IPUtil;
import com.magic.commons.enginegw.constants.Const;
import com.magic.commons.enginegw.util.SignUtil;
import com.magic.config.thrift.base.CmdType;
import com.magic.config.thrift.base.EGHeader;
import com.magic.config.thrift.base.EGReq;
import com.magic.config.thrift.base.EGResp;
import com.magic.config.thrift.base.Trace;
import com.magic.config.thrift.uranus.EGServer;

/**
 * ThriftFactory2
 *
 * @author zj
 * @date 2017/5/15
 */
public class ThriftFactory {

    /**
     * 连接池对象
     */
    private ConnectionPoolFactory connectionPoolFactory;

    /**
     * 构造函数
     * @param connectionPoolFactory
     */
    public ThriftFactory(ConnectionPoolFactory connectionPoolFactory) {
        this.connectionPoolFactory = connectionPoolFactory;
    }

    /**
     * thrift server 方法入口
     * @param type
     * @param cmd
     * @param body
     * @param caller
     * @return
     */
    public EGResp call(CmdType type, long cmd, String body, String caller){
        return call(type, cmd, body, null, caller);
    }

    /**
     *
     * @param type
     * @param cmd
     * @param body
     * @param token
     * @param caller
     * @return
     */
    public EGResp call(CmdType type, long cmd, String body, String token , String caller){
        EGReq req = new EGReq();
        EGHeader header = new EGHeader();
        header.setType(type);
        header.setCmd(cmd);
        if (StringUtils.isNoneEmpty(token)){
            header.setToken(token);
        }
        req.setHeader(header);
        req.setBody(body);
        return call(req, caller);
    }

    /**
     * thrift server 方法入口
     * @param req 请求参数
     * @param caller 调用方
     * @return
     */
    public EGResp call(EGReq req, String caller){
        return this.call(req, getTrace(caller));
    }

    /**
     * thrift server 方法入口
     * @param req 请求参数
     * @param trace 日记轨迹对象
     * @return
     */
    public EGResp call(EGReq req, Trace trace){
        req.getHeader().setVersion(Const.THRIFT_VERSION);
        req.getHeader().setTimestamp(System.currentTimeMillis());
        String signature = SignUtil.sign(req);
        req.getHeader().setSignature(signature);

        EGResp resp = null;
        TProtocol protocol = null;
        try {
            protocol = connectionPoolFactory.getConnection();
            EGServer.Client client = new EGServer.Client(protocol);
            resp = client.CallEGService(req, trace);
            EGResp logObj = toEgRespForLog(resp);
            ApiLogger.info(String.format("call gw. req: %s, trace: %s, resp: %s", JSON.toJSONString(req), JSON.toJSONString(trace), JSON.toJSONString(logObj)));
        } catch (Exception e){//重试
            try {
                if (protocol != null){
                    connectionPoolFactory.releaseConnection(protocol);
                    protocol = null;
                }
                protocol = connectionPoolFactory.getConnection();
                EGServer.Client client = new EGServer.Client(protocol);
                resp = client.CallEGService(req, trace);
                EGResp logObj = toEgRespForLog(resp);
                ApiLogger.info("try one resp:" + JSON.toJSONString(logObj));
            }catch (Exception e1){
                try {
                    if (protocol != null){
                        connectionPoolFactory.releaseConnection(protocol);
                        protocol = null;
                    }
                    protocol = connectionPoolFactory.getConnection();
                    EGServer.Client client = new EGServer.Client(protocol);
                    resp = client.CallEGService(req, trace);
                    EGResp logObj = toEgRespForLog(resp);
                    ApiLogger.info("try two resp:" + JSON.toJSONString(logObj));
                }catch (Exception e2){
                    ApiLogger.error(String.format("call engine gw thrift server error. req: %s, trace: %s", JSON.toJSONString(req), JSON.toJSONString(trace)), e2);
                }
            }
        }
        if (protocol != null){
            connectionPoolFactory.releaseConnection(protocol);
        }
        return resp;
    }

    /**
     * 对data进行截短，以减少日志量
     * @param resp
     * @return
     */
	private EGResp toEgRespForLog(EGResp resp) {
		if(resp==null){
			return null;
		}
		EGResp logObj=new EGResp();
		logObj.setCode(resp.getCode());
		logObj.setCodeIsSet(resp.isSetCode());
		logObj.setDataIsSet(resp.isSetData());
		logObj.setResultIsSet(resp.isSetResult());
		logObj.setResult(resp.getResult());
		String data=resp.getData();
		//自动截取100个字符
		if(data!=null && data.length()>100){
			data=data.substring(0,  100);
		}
		logObj.setData(data);
		return logObj;
	}

    /**
     * 组装trace对象
     * @param caller 调用方系统名 -- 确保全局唯一
     * @return
     */
    public Trace getTrace(String caller){
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
    private String assembleLogId(String caller) {
        StringBuilder logId = new StringBuilder();
        logId.append(IPUtil.ipToInt(IPUtil.getLocalIp()));
        logId.append(Const.SPILI_CHAR);
        logId.append(caller);
        logId.append(Const.SPILI_CHAR);
        logId.append(System.currentTimeMillis());
        return logId.toString();
    }

    public ConnectionPoolFactory getConnectionPoolFactory() {
        return connectionPoolFactory;
    }

    public void setConnectionPoolFactory(ConnectionPoolFactory connectionPoolFactory) {
        this.connectionPoolFactory = connectionPoolFactory;
    }
}
