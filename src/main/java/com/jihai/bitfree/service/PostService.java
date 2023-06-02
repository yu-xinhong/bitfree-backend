package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.dao.PostDAO;
import com.jihai.bitfree.dao.ReplyDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.PostDetailDTO;
import com.jihai.bitfree.dto.resp.PostItemDTO;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.entity.PostDO;
import com.jihai.bitfree.entity.ReplyDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.utils.DataConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    public List<PostItemDTO> pageQuery(Integer page, Integer size, Long topicId) {
        List<Long> topPostIdList = queryTopIdList();
        // 置顶的先查出来
        List<PostDO> topPostList = postDAO.queryByIdList(topPostIdList);

        size -= topPostList.size();
        List<PostDO> resultPostList = Lists.newArrayList();

        // 先填充置顶的帖子
        if (topPostList.size() > 0) {
            topPostList.forEach(post -> post.setTitle("【置顶】" + post.getTitle()));
            resultPostList.addAll(topPostList);
        }

        List<PostDO> postDOS = postDAO.pageQuery((page - 1) * size, size, topicId);
        postDOS.removeIf(postDO -> topPostIdList.contains(postDO.getId()));

        resultPostList.addAll(postDOS);
        ArrayList<PostItemDTO> postItemDTOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(resultPostList)) {
            return postItemDTOS;
        }

        List<Long> postIdList = resultPostList.stream().map(PostDO::getId).collect(Collectors.toList());
        // 查询回复数量
        List<ReplyDO> replyDOList = replyDAO.queryByPostIdList(postIdList);
        Map<Long, Long> replyCountMap = replyDOList.stream().collect(Collectors.groupingBy(ReplyDO::getPostId, Collectors.counting()));

        // 查询用户名称
        List<UserDO> userDOS = userDAO.batchQueryByIdList(resultPostList.stream().map(PostDO::getCreatorId).distinct().collect(Collectors.toList()));
        ImmutableMap<Long, UserDO> idUserMap = Maps.uniqueIndex(userDOS, UserDO::getId);

        List<PostItemDTO> pageList = resultPostList.stream().map(postDO -> {
            PostItemDTO postItemDTO = new PostItemDTO();
            postItemDTO.setId(postDO.getId());
            postItemDTO.setTitle(postDO.getTitle());
            postItemDTO.setCreatorName(idUserMap.get(postDO.getCreatorId()).getName());
            postItemDTO.setUpdateTime(postDO.getUpdateTime());
            Long count = replyCountMap.getOrDefault(postDO.getId(), 0L);
            if (count != null) postItemDTO.setReplyCount(count.intValue());
            return postItemDTO;
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

    public PostDetailDTO getDetail(Long id) {
        PostDO postDO = postDAO.getById(id);
        if (postDO == null) {
            log.error("risk id {} not exists in db", id);
            return null;
        }

        PostDetailDTO postDetailDTO = new PostDetailDTO();
        BeanUtils.copyProperties(postDO, postDetailDTO);

        UserDO userDO = userDAO.getById(postDO.getCreatorId());
        postDetailDTO.setCreatorName(userDO.getName());
        return postDetailDTO;
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
}
