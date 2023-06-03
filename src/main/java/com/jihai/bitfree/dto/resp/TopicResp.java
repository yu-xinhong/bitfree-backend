package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class TopicResp extends BaseResp {


    private static final long serialVersionUID = -7621167340762790576L;

    private Long id;

    private String name;

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
}
