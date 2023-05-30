package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import jakarta.validation.constraints.Size;

public class UserReplyReq extends BaseReq {

    private static final long serialVersionUID = 6395216939519368257L;

    private Long id;

    @Size(min = 1, message = "page不对")
    private Integer page;

    @Size(min = 0, message = "size不对")
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
