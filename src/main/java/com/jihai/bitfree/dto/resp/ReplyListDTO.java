package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseDTO;

import java.util.Date;
import java.util.List;

public class ReplyListDTO extends BaseDTO {

    private static final long serialVersionUID = -7308285401009752244L;

    private Long id;

    private String replyContent;

    private String name;

    private Long creatorId;

    private Date createTime;

    private List<ReplyListDTO> subReplyList;

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

    public List<ReplyListDTO> getSubReplyList() {
        return subReplyList;
    }

    public void setSubReplyList(List<ReplyListDTO> subReplyList) {
        this.subReplyList = subReplyList;
    }
}
