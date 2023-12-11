package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class OrderResp extends BaseResp {

    private Long id;

    private String detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
