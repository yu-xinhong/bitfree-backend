package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.*;

import javax.validation.constraints.Size;

public class PageQueryReq extends BaseReq {

    @Min(0)
    @Max(100)
    @NotNull(message = "页码不传的吗")
    private Integer page = 1;

    @Min(0)
    @Max(100)
    @NotNull(message = "每页size不传的吗")
    private Integer size = 20;


    private Long topicId;

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
