package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.enums.LikeTypeEnum;
import com.jihai.bitfree.base.enums.PostTypeEnum;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.*;
import com.jihai.bitfree.dto.resp.PostDetailResp;
import com.jihai.bitfree.dto.resp.PostItemResp;
import com.jihai.bitfree.dto.resp.RankPostItemResp;
import com.jihai.bitfree.dto.resp.VideoListResp;
import com.jihai.bitfree.entity.*;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.utils.DataConvert;
import com.jihai.bitfree.utils.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private ConfigDAO configDAO;

    @Autowired
    private ReplyDAO replyDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ReplyNoticeDAO replyNoticeDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private UserLikeDAO userLikeDAO;


    public List<PostItemResp> pageQuery(Integer page, Integer size, Long topicId, String searchText, Long userId, boolean includeTopList) {
        List<PostDO> resultPostList = Lists.newArrayList();
        List<PostDO> postDOS = postDAO.pageQuery((page - 1) * size, size, topicId, StringUtils.isEmpty(searchText) ? null : "%" + searchText.trim() + "%", userId);

        List<Long> topPostIdList;
        if (includeTopList && ! CollectionUtils.isEmpty(topPostIdList = queryTopIdList())) {
            // 置顶的先查出来
            List<PostDO> topPostList = postDAO.queryByIdList(topPostIdList);

            // 先填充置顶的帖子
            if (topPostList.size() > 0) {
                topPostList.forEach(post -> post.setTitle("【置顶】" + post.getTitle()));
                resultPostList.addAll(topPostList);
            }
            postDOS.removeIf(postDO -> topPostIdList.contains(postDO.getId()));
        }


        resultPostList.addAll(postDOS);
        ArrayList<PostItemResp> postItemResps = Lists.newArrayList();
        if (CollectionUtils.isEmpty(resultPostList)) {
            return postItemResps;
        }

        List<Long> postIdList = resultPostList.stream().map(PostDO::getId).collect(Collectors.toList());
        // 查询回复数量
        List<ReplyDO> replyDOList = CollectionUtils.isEmpty(postIdList) ? Lists.newArrayList() : replyDAO.queryByPostIdList(postIdList);
        Map<Long, Long> replyCountMap = replyDOList.stream().collect(Collectors.groupingBy(ReplyDO::getPostId, Collectors.counting()));

        // 查询用户名称
        List<UserDO> userDOS = CollectionUtils.isEmpty(resultPostList) ? Lists.newArrayList() : userDAO.batchQueryByIdList(resultPostList.stream().map(PostDO::getCreatorId).distinct().collect(Collectors.toList()));
        ImmutableMap<Long, UserDO> idUserMap = Maps.uniqueIndex(userDOS, UserDO::getId);


        List<UserDO> replyUserList = CollectionUtils.isEmpty(replyDOList) ? Lists.newArrayList() : userDAO.batchQueryByIdList(replyDOList.stream().map(ReplyDO::getSendUserId).collect(Collectors.toList()));
        ImmutableMap<Long, UserDO> replyUserIdMap = Maps.uniqueIndex(replyUserList, UserDO::getId);

        List<PostItemResp> pageList = resultPostList.stream().map(postDO -> {
            PostItemResp postItemResp = new PostItemResp();
            postItemResp.setId(postDO.getId());
            postItemResp.setAvatar(idUserMap.get(postDO.getCreatorId()).getAvatar());
            postItemResp.setTitle(postDO.getTitle());

            // 如果replyCount == 0
            postItemResp.setUpdateTime(postDO.getUpdateTime());
            postItemResp.setCreateTime(postDO.getCreateTime());
            Long replyCount = replyCountMap.getOrDefault(postDO.getId(), 0L);
            postItemResp.setCreatorName(idUserMap.get(postDO.getCreatorId()).getName());
            postItemResp.setNewPost(isNew(postDO.getCreateTime()));
                // 获取最新回复的人的名字
            if (replyCount > 0) {
                List<ReplyDO> curPostReplyList = replyDOList.stream().filter(replyDO -> replyDO.getPostId().equals(postDO.getId())).collect(Collectors.toList());
                curPostReplyList.sort((r1, r2) -> (int) (r2.getId() - r1.getId()));
                postItemResp.setUpdateUserName(replyUserIdMap.get(curPostReplyList.get(0).getSendUserId()).getName());
            }
            if (replyCount != null) postItemResp.setReplyCount(replyCount.intValue());
            return postItemResp;
        }).collect(Collectors.toList());

        return pageList;
    }

    public Integer count(Long topicId) {
        Integer count = postDAO.count(topicId);
        count = count - queryTopIdList().size();
        return count < 0 ? 0 : count;
    }

    private List<Long> queryTopIdList() {
        ConfigDO configDO = configDAO.getByKey(Constants.TOP_POST_ID);
        if (configDO == null) return Lists.newArrayList();
        return DataConvert.convertValue2List(configDO.getValue());
    }

    public PostDetailResp getDetail(Long id) {
        PostDO postDO = postDAO.getById(id);
        if (postDO == null) {
            log.error("risk id {} not exists in db", id);
            return null;
        }

        PostDetailResp postDetailResp = new PostDetailResp();
        BeanUtils.copyProperties(postDO, postDetailResp);

        UserDO userDO = userDAO.getById(postDO.getCreatorId());
        postDetailResp.setCreatorName(userDO.getName());
        postDetailResp.setAvatar(userDO.getAvatar());

        postDetailResp.setLikeCount(userLikeDAO.countLike(id, LikeTypeEnum.POST.getType()));

        // record view count
        postDAO.incrementView(id);
        return postDetailResp;
    }

    @Transactional
    public void add(String title, String content, Integer topicId, Long userId) {
        // 理论上要加锁，这里简单乐观锁实现, 因为并发概率很低
        userService.checkCoins(userId, 2);

        PostDO postDO = new PostDO();

        postDO.setCreatorId(userId);
        postDO.setTitle(title);
        postDO.setContent(content);
        postDO.setTopicId(topicId);
        postDO.setLastUpdaterId(userId);

        postDO.setType(getTypeContent(content));
        postDAO.insert(postDO);

        // 这里会再去判断coins >= 2
        userService.consumeCoins(userId, 2);
    }

    private Integer getTypeContent(String content) {
        if (! content.contains("[file")) return PostTypeEnum.POST.getType();
        Long fileId = getFileIdFromContent(content);
        if (fileId == null) return PostTypeEnum.POST.getType();
        FileDO fileDO = fileDAO.getById(fileId);
        return fileDO.getType() == FileUploadUtils.VIDEO_TYPE ?
                PostTypeEnum.VIDEO.getType() : PostTypeEnum.POST.getType();
    }

    private Long getFileIdFromContent(String content) {
        int startIndex = content.indexOf("[file:") + 9;
        return Long.valueOf(content.substring(startIndex, content.indexOf("]", startIndex)));
    }

    public Integer countByUserId(Long userId) {
        return postDAO.countByUserId(userId);
    }

    public List<RankPostItemResp> getRankList() {
        return postDAO.queryRankList();
    }

    @Transactional
    public Boolean deletePost(Long postId) {

        postDAO.deleted(postId);
        replyDAO.deleted(postId);
        replyNoticeDAO.deleted(postId);
        return true;
    }

    @Transactional
    public Boolean deleteReply(Long id) {
        replyDAO.deletedById(id);
        replyNoticeDAO.deletedByReplyId(id);
        replyDAO.deletedByTargetId(id);
        return true;
    }

    public PageResult<VideoListResp> pageQueryVideoList(Integer page, Integer size) {
        int total = postDAO.countVideo();
        List<PostDO> postDOList = postDAO.queryVideoList((page - 1) * size, size);
        if (CollectionUtils.isEmpty(postDOList)) return new PageResult<>(Collections.emptyList(), total);

        List<Long> userIdList = postDOList.stream().map(PostDO::getCreatorId).distinct().collect(Collectors.toList());
        List<UserDO> userDOList = userDAO.batchQueryByIdList(userIdList);
        ImmutableMap<Long, UserDO> userIdMap = Maps.uniqueIndex(userDOList, UserDO::getId);

        List<Long> fileIdList = postDOList.stream().map(post -> getFileIdFromContent(post.getContent())).distinct().collect(Collectors.toList());
        List<FileDO> fileDOList = fileDAO.batchQueryById(fileIdList);
        ImmutableMap<Long, FileDO> fileMap = Maps.uniqueIndex(fileDOList, FileDO::getId);

        List<VideoListResp> videoListRespList = postDOList.stream().map(post -> {
            VideoListResp videoListResp = new VideoListResp();
            videoListResp.setId(post.getId());
            videoListResp.setTitle(post.getTitle());
            videoListResp.setPoster(fileMap.get(getFileIdFromContent(post.getContent())).getPoster());
            videoListResp.setCreateTime(post.getCreateTime());
            videoListResp.setNewVideo(isNew(post.getCreateTime()));
            videoListResp.setCreatorName(userIdMap.get(post.getCreatorId()).getName());
            return videoListResp;
        }).collect(Collectors.toList());
        return new PageResult<>(videoListRespList, total);
    }

    private Boolean isNew(Date createTime) {
        // 3天内为新视频
        return new Date().getTime() - createTime.getTime() < 1000 * 60 * 60 * 24 * 3;
    }

    public void checkIsCurUserReply(Long id, Long userId) {
        ReplyDO replyDO = replyDAO.getById(id);
        if (replyDO == null) {
            log.warn("someone try to delete not exits post {} ", id);
            throw new BusinessException("帖子不存在");
        }
        if (! replyDO.getSendUserId().equals(userId)) {
            log.warn("someone try to delete else reply postId {}, userId {}", id, userId);
            throw new BusinessException("非法操作");
        }
    }
}
