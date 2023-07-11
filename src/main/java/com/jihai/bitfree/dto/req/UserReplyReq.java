package com.jihai.bitfree.dto.req;

import javax.validation.constraints.NotNull;

public class UserReplyReq extends PageQueryReq {

    @NotNull(message = "id不能为空")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
