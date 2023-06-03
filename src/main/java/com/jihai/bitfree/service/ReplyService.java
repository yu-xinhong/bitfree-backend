package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.dao.PostDAO;
import com.jihai.bitfree.dao.ReplyDAO;
import com.jihai.bitfree.dao.ReplyNoticeDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.ReplyListResp;
import com.jihai.bitfree.dto.resp.UserReplyResp;
import com.jihai.bitfree.entity.PostDO;
import com.jihai.bitfree.entity.ReplyDO;
import com.jihai.bitfree.entity.ReplyNoticeDO;
import com.jihai.bitfree.entity.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReplyService {

    @Autowired
    private ReplyDAO replyDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private ReplyNoticeDAO replyNoticeDAO;

    public List<ReplyListResp> getReplyList(Long id) {
        List<ReplyListResp> replyListResps = Lists.newArrayList();
        List<ReplyDO> replyDOList = replyDAO.getByPostId(id);
        if (CollectionUtils.isEmpty(replyDOList)) {
            return replyListResps;
        }

        List<Long> sendUserIdList = replyDOList.stream().map(ReplyDO::getSendUserId).collect(Collectors.toList());
        List<UserDO> userDOS = userDAO.batchQueryByIdList(sendUserIdList);

        ImmutableMap<Long, UserDO> idUserMap = Maps.uniqueIndex(userDOS, UserDO::getId);


        List<ReplyListResp> resultList = replyDOList.stream().map(replyDO -> convertReply2DTO(replyDO, idUserMap)).collect(Collectors.toList());
        resultList.sort((reply1, reply2) -> (int) (reply1.getCreateTime().getTime() - reply2.getCreateTime().getTime()));
        return resultList;
    }

    private ReplyListResp convertReply2DTO(ReplyDO mainReply, ImmutableMap<Long, UserDO> idUserMap) {
        ReplyListResp replyListResp = new ReplyListResp();

        replyListResp.setId(mainReply.getId());
        replyListResp.setReplyContent(mainReply.getReplyContent());
        replyListResp.setCreateTime(mainReply.getCreateTime());
        replyListResp.setCreatorId(mainReply.getSendUserId());

        replyListResp.setName(idUserMap.get(mainReply.getSendUserId()).getName());

        return replyListResp;
    }

    public Boolean reply(Long postId, Long replyId, Long userId, String replyContent) {
        PostDO postDO = postDAO.getById(postId);
        if (postDO == null) {
            log.error("post is null and risk postId {}, maybe someone scan posts ", postId);
            return false;
        }

        ReplyDO replyDO = new ReplyDO();
        replyDO.setPostId(postId);

        ReplyDO targetReply = null;
        if (replyId != null) {
            targetReply = replyDAO.getById(replyId);
            replyDO.setReceiverId(targetReply.getSendUserId());
        } else {
            replyDO.setReceiverId(postDO.getCreatorId());
        }
        replyDO.setTargetReplyId(replyId);

        replyDO.setReplyContent(replyContent);
        replyDO.setSendUserId(userId);

        // 这里写入后mybatis id会自动填充
        replyDAO.insert(replyDO);

        // 通知
        ReplyNoticeDO replyNoticeDO = new ReplyNoticeDO();
        replyNoticeDO.setPostId(postId);
        replyNoticeDO.setReplyId(replyDO.getId());

        // 此处可以考虑异步化
        // 发送通知, 此处如果回复的子评论，不通知到发帖人，只通知子评论的人
        if (targetReply != null) {
            if (targetReply.getSendUserId().equals(userId)) {
                // 自己回复自己的子回复不需要通知
                return true;
            }
            replyNoticeDO.setNotifyUserId(targetReply.getSendUserId());
        } else {
            // 评论的post
            if (postDO.getCreatorId().equals(userId)) {
                // 评论自己不回复
                return true;
            }
            replyNoticeDO.setNotifyUserId(postDO.getCreatorId());
        }
        replyNoticeDAO.insert(replyNoticeDO);
        return true;
    }

    public Integer replyCount(Long userId) {
        return replyNoticeDAO.countReplyCount(userId);
    }

    public Boolean read(Long userId) {
        replyNoticeDAO.updateStatus(userId);
        return true;
    }

    public PageResult<UserReplyResp> pageQueryUserReplyBySendUserId(Integer page, Integer size, Long userId) {
        List<ReplyDO> replyDOList = replyDAO.pageQueryBySendUserId(userId, (page - 1) * size, size);
        if (CollectionUtils.isEmpty(replyDOList)) return new PageResult<UserReplyResp>(Collections.EMPTY_LIST, 0);
        Integer count = replyDAO.countBySendUserId(userId);
        return buildPageQueryList(replyDOList, count);
    }

    private PageResult<UserReplyResp> buildPageQueryList(List<ReplyDO> replyDOList, Integer count) {
        List<Long> sendUserIdList = replyDOList.stream().map(replyDO -> replyDO.getSendUserId()).distinct().collect(Collectors.toList());
        List<UserDO> userDOS = userDAO.batchQueryByIdList(sendUserIdList);
        ImmutableMap<Long, UserDO> idUserMap = Maps.uniqueIndex(userDOS, UserDO::getId);


        List<ReplyDO> subReplyList = replyDOList.stream().filter(replyDO -> replyDO.getTargetReplyId() != null).collect(Collectors.toList());
        List<ReplyDO> postReplyList = replyDOList.stream().filter(replyDO -> replyDO.getTargetReplyId() == null).collect(Collectors.toList());
        // subReply reply  被回复的回复
        List<Long> replyIdList = subReplyList.stream().map(replyDO -> replyDO.getTargetReplyId()).distinct().collect(Collectors.toList());
        List<ReplyDO> haveRepliedReplyList = replyDAO.queryByIdList(replyIdList);
        ImmutableMap<Long, ReplyDO> hadRepliedMap = Maps.uniqueIndex(haveRepliedReplyList, ReplyDO::getId);

        List<Long> postIdList = postReplyList.stream().map(replyDO -> replyDO.getPostId()).distinct().collect(Collectors.toList());
        List<PostDO> haveRepliedPostList = postDAO.queryByIdList(postIdList);
        ImmutableMap<Long, PostDO> haveRepliedPostMap = Maps.uniqueIndex(haveRepliedPostList, PostDO::getId);

        List<UserReplyResp> resultList = replyDOList.stream().map(replyDO -> {
            UserReplyResp userReplyResp = new UserReplyResp();

            userReplyResp.setId(replyDO.getId());
            if (replyDO.getTargetReplyId() != null) {
                userReplyResp.setContent(hadRepliedMap.get(replyDO.getTargetReplyId()).getReplyContent());
            } else {
                userReplyResp.setContent(haveRepliedPostMap.get(replyDO.getPostId()).getContent());
            }

            userReplyResp.setReply(replyDO.getReplyContent());
            userReplyResp.setCreateTime(replyDO.getCreateTime());
            userReplyResp.setSendUserName(idUserMap.get(replyDO.getSendUserId()).getName());
            userReplyResp.setPostId(replyDO.getPostId());
            return userReplyResp;
        }).collect(Collectors.toList());

        return new PageResult<>(resultList, count);
    }

    public PageResult<UserReplyResp> pageQueryUserReplyByReceiverId(Integer page, Integer size, Long userId) {
        List<ReplyDO> replyDOList = replyDAO.pageQueryByReceiverId(userId, (page - 1) * size, size);
        if (CollectionUtils.isEmpty(replyDOList)) return new PageResult<>(Collections.EMPTY_LIST, 0);
        Integer count = replyDAO.countByReceiverId(userId);
        return buildPageQueryList(replyDOList, count);
    }
}
