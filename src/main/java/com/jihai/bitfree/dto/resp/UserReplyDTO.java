package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseDTO;

import java.util.Date;

public class UserReplyDTO extends BaseDTO {

    private static final long serialVersionUID = -876420912127120411L;

    private Long id;

    private String content;

    private String reply;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
