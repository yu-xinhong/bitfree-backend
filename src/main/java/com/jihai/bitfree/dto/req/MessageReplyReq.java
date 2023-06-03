package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

public class MessageReplyReq extends BaseReq {

    private static final long serialVersionUID = -6309954841622247201L;

    private Long id;

    private Integer page;

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
