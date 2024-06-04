package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.List;

public class QuestionNodeResp extends BaseResp {

    private Long id;

    private String title;

    private Long parentId;

    private String content;

    private Integer level;

    private Long userId;

    private Integer status;

    private List<QuestionNodeResp> subTreeNodeResp;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<QuestionNodeResp> getSubTreeNodeResp() {
        return subTreeNodeResp;
    }

    public void setSubTreeNodeResp(List<QuestionNodeResp> subTreeNodeResp) {
        this.subTreeNodeResp = subTreeNodeResp;
    }
}
