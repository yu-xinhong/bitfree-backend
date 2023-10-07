package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.Date;

public class VideoListResp extends BaseResp {

    private Long id;

    private String poster;

    private String creatorName;

    private Date createTime;

    private String title;

    private Boolean newVideo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getNewVideo() {
        return newVideo;
    }

    public void setNewVideo(Boolean newVideo) {
        this.newVideo = newVideo;
    }
}
