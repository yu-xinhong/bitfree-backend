package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class FileUploadResp extends BaseResp {

    private static final long serialVersionUID = 4690674995188258215L;

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
