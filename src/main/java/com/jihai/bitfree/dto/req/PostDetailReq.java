package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostDetailReq extends BaseReq {

    private static final long serialVersionUID = 9020247868100311340L;

    @Size(min = 0, message = "别刷了")
    @NotNull(message = "想啥呢，id都不传")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
