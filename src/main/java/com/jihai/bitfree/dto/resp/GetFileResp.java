package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class GetFileResp extends BaseResp {

    private static final long serialVersionUID = 333401767425702564L;

    private Long id;

    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
