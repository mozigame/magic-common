package com.magic.api.commons.atlas.core.mybatis;

import com.google.common.collect.Maps;
import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.api.commons.atlas.core.Page;
import com.magic.api.commons.atlas.core.PropertyFilter;
import com.magic.api.commons.atlas.utils.reflection.Reflections;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @param <T>
 * @param <PK>
 * @Doc 泛型Dao，提供多个数据源，根据id取余获取属于源
 * 需要在配置文件中配置bean idShardRange、sqlSessionShardTemplates、globalSqlSessionTemplate
 * 使用多数据源时，需要使用参数中带id的方法
 */
@SuppressWarnings("unchecked")
public class MyBatisDaoImpl<T, PK extends Serializable> implements BaseDao<T, PK> {

    /**
     * Entity的类型
     */
    protected Class<T> entityClass;

    /**
     * Entity的主键类型
     */
    protected Class<PK> pkClass;

    public String sqlMapNamespace = null;

    public static final String POSTFIX_INSERT = "insert";

    public static final String POSTFIX_UPDATE = "update";

    public static final String POSTFIX_DELETE = "delete";

    public static final String POSTFIX_GET = "get";

    public static final String POSTFIX_SELECT = "find";

    public static final String POSTFIX_SELECT_COUNT = "findCount";

    public static final String POSTFIX_SELECTPAGE = "findByPage";

    public static final String POSTFIX_SELECTPAGE_COUNT = "findByPageCount";

    public Map<Integer, Integer> dbModShardRange;

    protected Map<Integer, SqlSession> shardSqlSessionTemplates;

    protected SqlSession globalSqlSessionTemplate;
    /* 默认根据1024取余 */
    private static final int RANGE_SIZE = 1024;


    /**
     * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class. eg. public class UserDao extends
     * SimpleHibernateDao<User, Long>
     */
    public MyBatisDaoImpl() {
        this.entityClass = Reflections.getSuperClassGenricType(getClass());
        this.pkClass = Reflections.getSuperClassGenricType(getClass(), 1);
        this.sqlMapNamespace = entityClass.getName();
    }

    /**
     * @param shardRange
     * @Des 设置唯一id hash取余的范围
     */
    @Resource(name = "idShardRange")
    public void setDbModShardRange(Map<String, String> shardRange) {
        if (shardRange == null || shardRange.size() <= 0) {
            dbModShardRange = (Map<Integer, Integer>) Maps.newTreeMap().put(0, RANGE_SIZE);
        } else {
            dbModShardRange = Maps.newTreeMap();
            for (Map.Entry<String, String> entry : shardRange.entrySet()) {
                dbModShardRange.put(Integer.parseInt(entry.getKey()), Integer.parseInt(entry.getValue()));
            }
        }
    }

    /**
     * @param shardSqlSessionTemplates
     * @Des 设置多个sqlSessionTemplate，key为id hash取余的开始值
     */
    @Resource(name = "sqlSessionShardTemplates")
    public void setShardSqlSessionTemplate(Map<String, SqlSession> shardSqlSessionTemplates) {
        if (shardSqlSessionTemplates == null || shardSqlSessionTemplates.size() <= 0) {
            System.out.println("There is no sqlSessionTemplate");
            System.exit(0);
        } else {
            this.shardSqlSessionTemplates = Maps.newHashMap();
            for (Map.Entry<String, SqlSession> entry : shardSqlSessionTemplates.entrySet())
                this.shardSqlSessionTemplates.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }
    }

    /**
     * @param sqlSession
     * @Des 全局的sqlSessionTemplate，用于不跨mysql实例存储的业务使用
     */
    @Resource(name = "globalSqlSessionTemplate")
    public void setGlobalSqlSessionTemplate(SqlSession sqlSession) {
        this.globalSqlSessionTemplate = sqlSession;
    }

    /**
     * @return
     */
    public SqlSession getSqlSession() {
        return globalSqlSessionTemplate;
    }

    /**
     * @param id
     * @return
     * @Des 获取跨实例存储数据的sqlSessionTemplate
     */
    public SqlSession getSqlSession(final PK id) {
        int mod = id.hashCode() % RANGE_SIZE;
        for (Map.Entry<Integer, Integer> entry : dbModShardRange.entrySet()) {
            if (mod >= entry.getKey() && mod <= entry.getValue()) {
                return shardSqlSessionTemplates.get(entry.getKey());
            }
        }
        return null;
    }


    public String getSqlMapNamespace() {
        return sqlMapNamespace;
    }

    public PK insert(T entity) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        int num = getSqlSession().insert(
                sqlMapNamespace + "." + POSTFIX_INSERT, entity);
        return pkClass.getConstructor(String.class).newInstance(
                String.valueOf(num));
    }

    @Override
    public PK insert(PK id, T entity) throws Exception {
        int num = getSqlSession(id).insert(
                sqlMapNamespace + "." + POSTFIX_INSERT, entity);
        return pkClass.getConstructor(String.class).newInstance(
                String.valueOf(num));
    }

    public List<PK> insert(List<T> entitys) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<PK> pkList = new ArrayList<PK>();
        for (T e : entitys)
            pkList.add(null == e ? null : insert(e));
        return pkList;
    }

    @Override
    public List<PK> insert(Map<PK, T> entitys) throws Exception {
        List<PK> pkList = new ArrayList<PK>();

        for (Map.Entry<PK, T> e : entitys.entrySet())
            pkList.add(null == e ? null : insert(e.getKey(), e.getValue()));
        return pkList;
    }

    public Object insert(final String ql, final Object... values) throws Exception {
        int num = 0;
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                num += getSqlSession().insert(sqlMapNamespace + "." + ql,
                        values[i]);
            }
        } else {
            num += getSqlSession().insert(sqlMapNamespace + "." + ql);
        }
        return num;
    }

    @Override
    public Object insert(PK id, String ql, Object... values) throws Exception {
        int num = 0;
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                num += getSqlSession(id).insert(sqlMapNamespace + "." + ql,
                        values[i]);
            }
        } else {
            num += getSqlSession(id).insert(sqlMapNamespace + "." + ql);
        }
        return num;
    }

    public int delete(T entity) throws Exception {
        return getSqlSession().delete(sqlMapNamespace + "." + POSTFIX_DELETE,
                entity);
    }

    @Override
    public int delete(PK id, T entity) throws Exception {
        return getSqlSession(id).delete(sqlMapNamespace + "." + POSTFIX_DELETE,
                entity);
    }

    public int delete(List<T> entitys) throws Exception {
        int rowsEffected = 0;
        for (T e : entitys)
            rowsEffected += null == e ? 0 : delete(e);
        return rowsEffected;
    }

    @Override
    public int delete(final Map<PK, T> entitys) throws Exception {
        int rowsEffected = 0;
        for (Map.Entry<PK, T> e : entitys.entrySet())
            rowsEffected += null == e ? 0 : delete(e.getKey(), e.getValue());
        return rowsEffected;
    }

    public int delete(final String ql, final Object... values) throws Exception {
        int num = 0;
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                num += getSqlSession().delete(sqlMapNamespace + "." + ql,
                        values[i]);
            }
        } else {
            num += getSqlSession().delete(sqlMapNamespace + "." + ql);
        }
        return num;
    }

    @Override
    public int delete(PK id, String ql, Object... values) throws Exception {
        int num = 0;
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                num += getSqlSession(id).delete(sqlMapNamespace + "." + ql,
                        values[i]);
            }
        } else {
            num += getSqlSession(id).delete(sqlMapNamespace + "." + ql);
        }
        return num;
    }

    public int update(T entity) throws Exception {
        return getSqlSession().update(sqlMapNamespace + "." + POSTFIX_UPDATE, entity);
    }

    @Override
    public int update(PK id, T entity) throws Exception {
        return getSqlSession(id).update(sqlMapNamespace + "." + POSTFIX_UPDATE, entity);
    }

    public int update(List<T> entity) throws Exception {
        int rowsEffected = 0;
        for (T e : entity)
            rowsEffected += null == e ? 0 : update(e);
        return rowsEffected;
    }

    @Override
    public int update(Map<PK, T> entitys) throws Exception {
        int rowsEffected = 0;
        for (Map.Entry<PK, T> e : entitys.entrySet())
            rowsEffected += null == e ? 0 : update(e.getKey(), e.getValue());
        return rowsEffected;
    }

    public int update(final String ql, final Object... values) throws Exception {
        int num = 0;
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                num += getSqlSession().update(sqlMapNamespace + "." + ql,
                        values[i]);
            }
        } else {
            num += getSqlSession().update(sqlMapNamespace + "." + ql);
        }
        return num;
    }

    @Override
    public int update(String ql, PK id, Object... values) throws Exception {
        int num = 0;
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                num += getSqlSession(id).update(sqlMapNamespace + "." + ql,
                        values[i]);
            }
        } else {
            num += getSqlSession(id).update(sqlMapNamespace + "." + ql);
        }
        return num;
    }

    public T get(final PK id) throws Exception {
        return (T) getSqlSession().selectOne(
                sqlMapNamespace + "." + POSTFIX_GET, id);
    }

    @Override
    public T get(PK id, boolean isShard) throws Exception {
        if (isShard)
            return (T) getSqlSession(id).selectOne(
                    sqlMapNamespace + "." + POSTFIX_GET, id);
        else
            return (T) getSqlSession().selectOne(
                    sqlMapNamespace + "." + POSTFIX_GET, id);
    }

    public Object get(final String ql, final Object... values) throws Exception {
        if (values != null) {
            Object result = null;
            for (int i = 0; i < values.length; i++) {
                result = getSqlSession().selectOne(sqlMapNamespace + "." + ql,
                        values[i]);
            }
            return result;
        } else {
            return getSqlSession().selectOne(sqlMapNamespace + "." + ql);
        }
    }

    @Override
    public Object get(String ql, PK id, Object... values) throws Exception {
        if (values != null) {
            Object result = null;
            for (int i = 0; i < values.length; i++) {
                result = getSqlSession(id).selectOne(sqlMapNamespace + "." + ql,
                        values[i]);
            }
            return result;
        } else {
            return getSqlSession(id).selectOne(sqlMapNamespace + "." + ql);
        }
    }

    public List<T> find(final T entity) throws Exception {
        return getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECT, entity);
    }

    public final long findCount(final T entity) throws Exception {
        return getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECT, entity).size();
    }

    public List<T> find(final String ql, final Object... values) throws Exception {
        if (values != null) {
            List<T> result = null;
            for (int i = 0; i < values.length; i++) {
                result = getSqlSession().selectList(sqlMapNamespace + "." + ql, values[i]);
            }
            return result;
        } else {
            return getSqlSession().selectList(sqlMapNamespace + "." + ql);
        }
    }

    public long findCount(final String ql, final Object... values) throws Exception {
        Long result = null;
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                result = (Long) getSqlSession().selectOne(sqlMapNamespace + "." + ql, values[i]);
            }
        } else {
            result = (Long) getSqlSession().selectOne(sqlMapNamespace + "." + ql);
        }
        return result.longValue();
    }

    public Page<T> find(final Page<T> page, final T entity) throws Exception {
        RowBounds rowBounds = new RowBounds((page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        page.setResult((List<T>) getSqlSession().selectList(sqlMapNamespace + "." + POSTFIX_SELECTPAGE, entity, rowBounds));
        page.setTotalCount(findCount(POSTFIX_SELECTPAGE_COUNT, entity));
        return page;
    }

    public Page<T> find(final Page<T> page, final String ql, final Object... values) throws Exception {
        RowBounds rowBounds = new RowBounds((page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
        page.setResult((List<T>) getSqlSession().selectList(sqlMapNamespace + "." + ql, values, rowBounds));
        page.setTotalCount(findCount(ql + "Count", values));
        return page;
    }

    public Page<T> find(Page<T> page, List<PropertyFilter> filters)
            throws Exception {
        return null;
    }
}
