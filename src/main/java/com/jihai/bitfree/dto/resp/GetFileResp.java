package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class GetFileResp extends BaseResp {

    private static final long serialVersionUID = 333401767425702564L;

    private Long id;

    private String url;

    /**
     * 1-video, 2-image
     */
    private Integer type;

    private String poster;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
