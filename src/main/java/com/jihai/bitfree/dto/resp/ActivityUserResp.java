package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class ActivityUserResp extends BaseResp  {

    private static final long serialVersionUID = 5880013869981890755L;

    private Long id;

    private String name;

    private String avatar;

    private Integer count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
