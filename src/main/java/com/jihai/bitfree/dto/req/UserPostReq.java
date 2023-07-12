package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UserPostReq extends BaseReq {

    private static final long serialVersionUID = -5153444286900440085L;

    private Long id;

    @Min(0)
    @Max(100)
    @NotNull(message = "page不能为空")
    private Integer page;

    @Min(0)
    @Max(100)
    @NotNull(message = "size不能为空")
    private Integer size;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
