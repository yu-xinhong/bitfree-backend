package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;

public class CollectReq extends BaseReq {

    private Long postId;

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
