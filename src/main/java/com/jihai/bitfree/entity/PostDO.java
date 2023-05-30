package com.jihai.bitfree.entity;

import com.jihai.bitfree.base.BaseDO;

public class PostDO extends BaseDO {

    private static final long serialVersionUID = -8985472826494371103L;

    private String title;

    private String content;

    private Long creatorId;

    private String topicId;

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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public Long getLastUpdaterId() {
        return lastUpdaterId;
    }

    public void setLastUpdaterId(Long lastUpdaterId) {
        this.lastUpdaterId = lastUpdaterId;
    }
}
