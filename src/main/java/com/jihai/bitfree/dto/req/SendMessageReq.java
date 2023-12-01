package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.aspect.SensitiveText;
import com.jihai.bitfree.base.BaseReq;
import org.hibernate.validator.constraints.Length;

public class SendMessageReq extends BaseReq {

    @Length(max = 400, message = "消息不能太长哦")
    @SensitiveText
    private String content;

    private Long replyMessageId;

    private Long atUser;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReplyMessageId() {
        return replyMessageId;
    }

    public void setReplyMessageId(Long replyMessageId) {
        this.replyMessageId = replyMessageId;
    }

    public Long getAtUser() {
        return atUser;
    }

    public void setAtUser(Long atUser) {
        this.atUser = atUser;
    }
}
