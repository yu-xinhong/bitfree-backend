package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.aspect.SensitiveText;
import com.jihai.bitfree.base.BaseReq;

import javax.validation.constraints.NotNull;

public class AddPostReq extends BaseReq {

    private static final long serialVersionUID = 2399161127153942996L;

    @NotNull(message = "标题不能为空")
    @SensitiveText
    private String title;

    @NotNull(message = "内容不能为空")
    @SensitiveText
    private String content;

    @NotNull(message = "topic不能为空")
    private Integer topicId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
