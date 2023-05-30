package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PageQueryReq extends BaseReq {

    @Size(min = 1, max = 200, message = "别搞事，小心封号")
    @NotNull(message = "页码不传的吗")
    private Integer page;

    @Size(min = 20, max = 50, message = "别刷了，再搞封号")
    @NotNull(message = "每页size不传的吗")
    private Integer size;

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
