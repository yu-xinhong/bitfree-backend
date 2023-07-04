package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class FileDO extends BaseDO {

    private static final long serialVersionUID = 4864186812728031142L;

    private Integer type;

    private String format;

    private String url;

    private Long userId;

    private String name;

    private String poster;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
