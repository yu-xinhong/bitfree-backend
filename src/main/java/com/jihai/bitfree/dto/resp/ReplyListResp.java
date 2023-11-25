package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.Date;
import java.util.List;

public class ReplyListResp extends BaseResp {

    private static final long serialVersionUID = -7308285401009752244L;

    private Long id;

//    private String avatar;

    private String replyContent;

//    private String name;

//    private Long creatorId;

    private Date createTime;

    private Boolean like;

    private Integer likeCount;

    private Long targetReplyId;

    private UserResp sendUser;

    private UserResp receiveUser;

    private List<ReplyListResp> subReplyList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public String getAvatar() {
//        return avatar;
//    }
//
//    public void setAvatar(String avatar) {
//        this.avatar = avatar;
//    }

    public Long getTargetReplyId() {
        return targetReplyId;
    }

    public void setTargetReplyId(Long targetReplyId) {
        this.targetReplyId = targetReplyId;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

//    public Long getCreatorId() {
//        return creatorId;
//    }
//
//    public void setCreatorId(Long creatorId) {
//        this.creatorId = creatorId;
//    }

    public Date getCreateTime() {
        return createTime;
    }

    public UserResp getSendUser() {
        return sendUser;
    }

    public void setSendUser(UserResp sendUser) {
        this.sendUser = sendUser;
    }

    public UserResp getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(UserResp receiveUser) {
        this.receiveUser = receiveUser;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

    public List<ReplyListResp> getSubReplyList() {
        return subReplyList;
    }

    public void setSubReplyList(List<ReplyListResp> subReplyList) {
        this.subReplyList = subReplyList;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
}
