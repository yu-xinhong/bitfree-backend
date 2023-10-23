package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PageQueryReq extends BaseReq {

    @Min(0)
    @Max(100)
    @NotNull(message = "页码不传的吗")
    private Integer page = 1;

    @Min(0)
    @Max(1000)
    @NotNull(message = "每页size不传的吗")
    private Integer size = 20;


    private Long topicId;

    @Length(max = 200)
    private String searchText;

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

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
