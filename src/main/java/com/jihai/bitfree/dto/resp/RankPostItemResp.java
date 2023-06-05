package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

public class RankPostItemResp extends BaseResp {

    private static final long serialVersionUID = 211575867077036638L;

    private Long id;

    private String title;

    private Integer replyCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }
}
