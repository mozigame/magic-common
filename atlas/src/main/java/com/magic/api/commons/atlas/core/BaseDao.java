package com.magic.api.commons.atlas.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDao<T, PK extends Serializable> {
    /**
     * 保存新增对象.
     */
    PK insert(final T entity) throws Exception;

    /**
     * 保存新增对象.根据id获取数据源
     */
    PK insert(final PK id, final T entity) throws Exception;

    /**
     * 保存新增对象列表.
     */
    List<PK> insert(final List<T> entitys) throws Exception;

    /**
     * 保存新增对象列表.根据id获取数据源
     */
    List<PK> insert(final Map<PK, T> entitys) throws Exception;

    /**
     * 保存新增对象.
     */
    Object insert(final String ql, final Object... values) throws Exception;

    /**
     * 保存新增对象.根据id获取数据源
     */
    Object insert(final PK id, final String ql, final Object... values) throws Exception;

    /**
     * 删除对象.
     *
     * @param entity 对象必须是session中的对象或含id属性的transient对象.
     */
    int delete(final T entity) throws Exception;

    /**
     * 删除对象.根据id获取数据源
     *
     * @param entitys 对象必须是session中的对象或含id属性的transient对象.
     */
    int delete(final PK id, final T entity) throws Exception;

    /**
     * 删除对象列表.
     *
     * @param entitys 对象必须是session中的对象或含id属性的transient对象.
     */
    int delete(final List<T> entitys) throws Exception;

    /**
     * 删除对象列表.根据id获取数据源
     *
     * @param entitys 对象必须是session中的对象或含id属性的transient对象.
     */
    int delete(final Map<PK, T> entitys) throws Exception;

    /**
     * 删除对象.
     *
     * @param values 删除语句中的参数.
     */
    int delete(final String ql, final Object... values) throws Exception;

    /**
     * 删除对象.根据id获取数据源
     *
     * @param values 删除语句中的参数.
     */
    int delete(final PK id, final String ql, final Object... values) throws Exception;

    /**
     * 保存修改的对象.
     * <p>
     * * @param entity 对象必须是session中的对象或含id属性的transient对象.
     */
    int update(final T entity) throws Exception;

    /**
     * 保存修改的对象.根据id获取数据源
     * <p>
     * * @param entity 对象必须是session中的对象或含id属性的transient对象.
     */
    int update(final PK id, final T entity) throws Exception;

    /**
     * 保存修改的对象列表.
     */
    int update(final List<T> entitys) throws Exception;

    /**
     * 保存修改的对象列表.
     */
    int update(final Map<PK, T> entitys) throws Exception;

    /**
     * 保存修改的对象.根据id获取数据源
     */
    int update(final String ql, final Object... values) throws Exception;

    /**
     * 保存修改的对象.根据id获取数据源
     */
    int update(final String ql, PK id, final Object... values) throws Exception;

    /**
     * 按id获取对象.
     */
    T get(final PK id) throws Exception;

    /**
     * 按id获取对象.根据id获取数据源
     */
    T get(final PK id, boolean isShard) throws Exception;

    /**
     * 获取对象.
     */
    Object get(final String ql, final Object... values) throws Exception;

    /**
     * 获取对象.根据id获取数据源
     */
    Object get(final String ql, PK id, final Object... values) throws Exception;


    /**
     * 查询对象列表.
     *
     * @param entity 参数对象.
     * @return List<T> 查询结果对象列表
     */
    List<T> find(T entity) throws Exception;


    /**
     * 查询对象列表的数量.
     *
     * @param entity 参数对象.
     * @return 查询结果的数量
     */
    long findCount(T entity) throws Exception;

    /**
     * 查询对象列表.
     *
     * @param hql
     * @param values 参数对象.
     * @return List<X> 查询结果对象列表
     */
    <X> List<X> find(final String hql, final Object... values) throws Exception;

    /**
     * 查询对象列表的数量.
     *
     * @param ql
     * @param values 参数对象.
     * @return 查询结果的数量
     */
    long findCount(final String ql, final Object... values) throws Exception;

    /**
     * 分页查询对象列表.
     *
     * @param page   分页参数对象
     * @param entity 查询参数对象.
     * @return Page<T> 查询结果的分页对象
     */
    Page<T> find(Page<T> page, final T entity) throws Exception;

    /**
     * 分页查询对象列表.
     *
     * @param page   分页参数对象
     * @param ql
     * @param values 查询参数对象.
     * @return Page<T> 查询结果的分页对象
     */
    Page<T> find(Page<T> page, final String ql, final Object... values) throws Exception;

    Page<T> find(final Page<T> page, final List<PropertyFilter> filters) throws Exception;
}
