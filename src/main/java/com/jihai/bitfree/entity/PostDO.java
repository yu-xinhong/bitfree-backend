package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class PostDO extends BaseDO {

    private static final long serialVersionUID = -8985472826494371103L;

    private String title;

    private String content;

    private Integer viewCount;

    private Integer type;

    private Long creatorId;

    private Integer topicId;

    private Long lastUpdaterId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Long getLastUpdaterId() {
        return lastUpdaterId;
    }

    public void setLastUpdaterId(Long lastUpdaterId) {
        this.lastUpdaterId = lastUpdaterId;
    }
}
