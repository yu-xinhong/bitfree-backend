package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class ActivityUserResp extends BaseResp  {

    private static final long serialVersionUID = 5880013869981890755L;

    private String name;

    private Integer count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
