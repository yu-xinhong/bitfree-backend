package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;


public class CoinsRecordTypeResp extends BaseResp {
    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
