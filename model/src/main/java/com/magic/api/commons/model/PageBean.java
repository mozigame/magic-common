package com.magic.api.commons.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 分页对象
 * @author zz
 */
public class PageBean<Z> implements Serializable {

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页数据量
     */
    private Integer count;

    /**
     * 总数据量
     */
    private Long total;

    /**
     * 当前游标
     */
    private Long cursor;

    /**
     * 下一个游标 没有-1
     */
    private Long next;

    /**
     * 分页数据
     */
    private Collection<Z> list;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getCursor() {
        return cursor;
    }

    public void setCursor(Long cursor) {
        this.cursor = cursor;
    }

    public Long getNext() {
        return next;
    }

    public void setNext(Long next) {
        this.next = next;
    }

    public Collection<Z> getList() {
        return list;
    }

    public void setList(Collection<Z> list) {
        if (list == null){
            list = new ArrayList<>();
        }
        this.list = list;
    }
}
