package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

public class FileUploadReq extends BaseReq {

    private static final long serialVersionUID = 7588929113874657328L;

    private String fileUrl;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
