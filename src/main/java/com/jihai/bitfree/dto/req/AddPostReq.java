package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import javax.validation.constraints.NotNull;

public class AddPostReq extends BaseReq {

    private static final long serialVersionUID = 2399161127153942996L;

    @NotNull(message = "标题为空")
    private String title;

    @NotNull(message = "内容为空")
    private String content;

    @NotNull(message = "topic为空")
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
