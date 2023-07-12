package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.NotNull;

public class FileUploadReq extends BaseReq {

    private static final long serialVersionUID = 7588929113874657328L;

    @NotNull(message = "fileUrl 不能为空")
    private String fileUrl;

    private String poster;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
