package com.magic.api.commons.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * 分页对象
 * @author zz
 */
public class PageBean<Z> implements Serializable {

    /**
     * 当前页
     */
    private int page;

    /**
     * 每页数据量
     */
    private int count;

    /**
     * 总数据量
     */
    private long total;

    /**
     * 当前游标
     */
    private long cursor;

    /**
     * 下一个游标 没有-1
     */
    private long next;

    /**
     * 分页数据
     */
    private Collection<Z> list;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getCursor() {
        return cursor;
    }

    public void setCursor(long cursor) {
        this.cursor = cursor;
    }

    public long getNext() {
        return next;
    }

    public void setNext(long next) {
        this.next = next;
    }

    public Collection<Z> getList() {
        return list;
    }

    public void setList(Collection<Z> list) {
        this.list = list;
    }
}
