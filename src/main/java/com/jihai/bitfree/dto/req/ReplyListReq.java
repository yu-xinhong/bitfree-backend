package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReplyListReq extends BaseReq {


    private static final long serialVersionUID = -7442889810911099673L;

    @NotNull(message = "别刷数据了")
    @Size(min = 0)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
