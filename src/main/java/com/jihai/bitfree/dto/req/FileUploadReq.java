package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

public class FileUploadReq extends BaseReq {

    private static final long serialVersionUID = 7588929113874657328L;

    private String videoUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
