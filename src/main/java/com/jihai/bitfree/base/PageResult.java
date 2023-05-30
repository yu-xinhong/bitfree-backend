package com.jihai.bitfree.base;

import java.util.List;

public class PageResult<T> {

    private List<T> list;

    private Integer total;

    public PageResult(List<T> list, Integer total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
