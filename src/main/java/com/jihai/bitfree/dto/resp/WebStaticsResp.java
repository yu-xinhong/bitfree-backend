package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class WebStaticsResp extends BaseResp {

    private Integer requestCount;

    private Integer userLoginCount;

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getUserLoginCount() {
        return userLoginCount;
    }

    public void setUserLoginCount(Integer userLoginCount) {
        this.userLoginCount = userLoginCount;
    }

    @Override
    public String toString() {
        return "WebStaticsResp{" +
                "requestCount=" + requestCount +
                ", userLoginCount=" + userLoginCount +
                '}';
    }
}
