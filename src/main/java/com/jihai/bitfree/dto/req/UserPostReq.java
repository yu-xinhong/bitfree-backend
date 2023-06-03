package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import jakarta.validation.constraints.Size;

public class UserPostReq extends BaseReq {

    private static final long serialVersionUID = -5153444286900440085L;

    private Long id;

    @Size(min = 0, message = "页码不合法")
    private Integer page;

    @Size(min = 0, max = 100, message = "长度不支持")
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
