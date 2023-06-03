package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.Date;
import java.util.List;

public class ReplyListResp extends BaseResp {

    private static final long serialVersionUID = -7308285401009752244L;

    private Long id;

    private String replyContent;

    private String name;

    private Long creatorId;

    private Date createTime;

    private List<ReplyListResp> subReplyList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<ReplyListResp> getSubReplyList() {
        return subReplyList;
    }

    public void setSubReplyList(List<ReplyListResp> subReplyList) {
        this.subReplyList = subReplyList;
    }
}
