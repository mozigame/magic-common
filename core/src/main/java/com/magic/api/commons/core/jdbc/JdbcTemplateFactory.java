package com.magic.api.commons.core.jdbc;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.tools.HashUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zz
 */
public class JdbcTemplateFactory {

    private int groupBy;//表数量
    private int databaseGroupBy;//数据库数量
    private List<JdbcTemplate> jdbcTemplateMasters;//主
    private Map<JdbcTemplate, Integer> JdbcTemplateIndexMapping = new HashMap<JdbcTemplate, Integer>();
    private List<JdbcTemplate> proxyJdbcTemplateSlaves = new ArrayList<JdbcTemplate>();//代理
    private List<List<JdbcTemplate>> jdbcTemplateSlaves;//从
    private JdbcTemplateProxy jdbcTemplateProxy = new JdbcTemplateProxy();//代理生成类
    private static final int SLOW_SQL = 100;

    public JdbcTemplateFactory(int groupBy, List<JdbcTemplate> jdbcTemplateMasters, List<List<JdbcTemplate>> jdbcTemplateSlaves) {
        this.groupBy = groupBy;
        this.jdbcTemplateMasters = jdbcTemplateMasters;
        this.jdbcTemplateSlaves = jdbcTemplateSlaves;
        databaseGroupBy = groupBy / jdbcTemplateMasters.size();
        for (int i = 0; i < jdbcTemplateMasters.size(); i++) {
            JdbcTemplate proxy = jdbcTemplateProxy.getProxy(JdbcTemplate.class);
            JdbcTemplateIndexMapping.put(proxy, i);
            proxyJdbcTemplateSlaves.add(proxy);
        }
    }

    public JdbcTemplate getJdbcTemplate(long id) {
        return getJdbcTemplate(id, false);
    }

    public JdbcTemplate getJdbcTemplate(long id, boolean useMaster) {
        int JdbcTemplateIndex = (int)(Math.abs(id) % 100) / databaseGroupBy;
        if (useMaster || RequestContext.getRequestContext().isReadMaster()) {
            return jdbcTemplateMasters.get(JdbcTemplateIndex);
        }
        return proxyJdbcTemplateSlaves.get(JdbcTemplateIndex);
    }

    public JdbcTemplate getJdbcTemplate(String id) {
        return getJdbcTemplate(id, false);
    }

    public JdbcTemplate getJdbcTemplate(String id, boolean useMaster) {
        return getJdbcTemplate(HashUtil.getIndex(id), useMaster);
    }

    private JdbcTemplate getJdbcTemplateByIndex(int index, boolean useMaster) {
        if (useMaster || RequestContext.getRequestContext().isReadMaster()) {
            return jdbcTemplateMasters.get(index);
        }
        List<JdbcTemplate> jdbcTemplates = jdbcTemplateSlaves.get(index);
        return jdbcTemplates.get(RandomUtils.nextInt(0, jdbcTemplates.size()));
    }

    private int getJdbcTemplateProxyIndex(JdbcTemplate jdbcTemplate) {
        return JdbcTemplateIndexMapping.get(jdbcTemplate);
    }

    private class JdbcTemplateProxy implements MethodInterceptor {

        public <T> T getProxy(Class<T> cls) {
            Enhancer enhancer = new Enhancer();
            return (T)enhancer.create(cls, jdbcTemplateProxy);
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            String methodName = method.getName();
            switch (methodName) {
                case "hashCode":
                case "equals":
                    return methodProxy.invokeSuper(o, objects);
            }
            JdbcTemplate proxyJdbcTemplate = (JdbcTemplate)o;
            int index = getJdbcTemplateProxyIndex(proxyJdbcTemplate);
            JdbcTemplate jdbcTemplate;
            if (methodName.startsWith("query")) {
                jdbcTemplate = getJdbcTemplateByIndex(index, false);
            } else {
                jdbcTemplate = getJdbcTemplateByIndex(index, true);
            }

            long currentTimeMillis = System.currentTimeMillis();
            Object invoke = method.invoke(jdbcTemplate, objects);
            long l = System.currentTimeMillis() - currentTimeMillis;
            if (SLOW_SQL <= l) {
                ApiLogger.dbInfo(methodName + " 耗时 " + l + " 参数 " + JSON.toJSONString(objects));
            } else {
                ApiLogger.debug("SQL Debug " + JSON.toJSONString(objects));
            }
            //TODO 完善log信息
            return invoke;
        }
    }
}
