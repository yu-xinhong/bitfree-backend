package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.*;
import com.jihai.bitfree.dto.resp.RankPostItemResp;
import com.jihai.bitfree.dto.resp.PostDetailResp;
import com.jihai.bitfree.dto.resp.PostItemResp;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.entity.PostDO;
import com.jihai.bitfree.entity.ReplyDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.utils.DataConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.management.RuntimeMBeanException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    public List<PostItemResp> pageQuery(Integer page, Integer size, Long topicId, Long userId, boolean includeTopList) {
        List<PostDO> resultPostList = Lists.newArrayList();
        List<PostDO> postDOS = postDAO.pageQuery((page - 1) * size, size, topicId, userId);

        if (includeTopList) {
            List<Long> topPostIdList = queryTopIdList();
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
        List<ReplyDO> replyDOList = replyDAO.queryByPostIdList(postIdList);
        Map<Long, Long> replyCountMap = replyDOList.stream().collect(Collectors.groupingBy(ReplyDO::getPostId, Collectors.counting()));

        // 查询用户名称
        List<UserDO> userDOS = userDAO.batchQueryByIdList(resultPostList.stream().map(PostDO::getCreatorId).distinct().collect(Collectors.toList()));
        ImmutableMap<Long, UserDO> idUserMap = Maps.uniqueIndex(userDOS, UserDO::getId);


        List<UserDO> replyUserList = userDAO.batchQueryByIdList(replyDOList.stream().map(ReplyDO::getSendUserId).collect(Collectors.toList()));
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
                // 获取最新回复的人的名字
            if (replyCount > 0) {
                List<ReplyDO> curPostReplyList = replyDOList.stream().filter(replyDO -> replyDO.getPostId().equals(postDO.getId())).collect(Collectors.toList());
                curPostReplyList.sort((r1, r2) -> (int) (r2.getCreateTime().getTime() - r1.getCreateTime().getTime()));
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
        return postDetailResp;
    }

    public void add(String title, String content, Integer topicId, Long userId) {
        PostDO postDO = new PostDO();

        postDO.setCreatorId(userId);
        postDO.setTitle(title);
        postDO.setContent(content);
        postDO.setTopicId(topicId);
        postDO.setLastUpdaterId(userId);
        postDAO.insert(postDO);
    }

    public Integer countByUserId(Long userId) {
        return postDAO.countByUserId(userId);
    }

    public List<RankPostItemResp> getRankList() {
        return postDAO.queryRankList();
    }

    @Transactional
    public Boolean deletePost(Long postId, String secret) {
        ConfigDO config = configDAO.getByKey(Constants.SECRET);
        if (config == null) {
            throw new RuntimeException("secret not config !");
        }
        if (! config.getValue().equals(secret)) {
            log.warn("secret is error ! {}", secret);
            throw new RuntimeException("secret error");
        }
        postDAO.deleted(postId);
        replyDAO.deleted(postId);
        replyNoticeDAO.deleted(postId);
        return true;
    }
}
