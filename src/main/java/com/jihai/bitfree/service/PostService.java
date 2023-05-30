package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.dao.PostDAO;
import com.jihai.bitfree.dao.ReplyDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.PostItemDTO;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.entity.PostDO;
import com.jihai.bitfree.entity.ReplyDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.utils.DataConvert;
import lombok.extern.slf4j.Slf4j;
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

    public List<PostItemDTO> pageQuery(Integer page, Integer size) {
        List<Long> topPostIdList = queryTopIdList();
        size -= topPostIdList.size();

        List<PostDO> postDOS = postDAO.pageQuery(page, size);

        ArrayList<PostItemDTO> postItemDTOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(postDOS)) {
            return postItemDTOS;
        }

        List<Long> postIdList = postDOS.stream().map(PostDO::getId).collect(Collectors.toList());
        // 查询回复数量
        List<ReplyDO> replyDOList = replyDAO.countByPostIdList(postIdList);
        Map<Long, Long> replyCountMap = replyDOList.stream().collect(Collectors.groupingBy(ReplyDO::getPostId, Collectors.counting()));

        // 查询用户名称
        List<UserDO> userDOS = userDAO.batchQueryByIdList(postDOS.stream().map(PostDO::getCreatorId).collect(Collectors.toList()));
        ImmutableMap<Long, UserDO> idUserMap = Maps.uniqueIndex(userDOS, UserDO::getId);

        return postDOS.stream().map(postDO -> {
            PostItemDTO postItemDTO = new PostItemDTO();
            postItemDTO.setId(postDO.getId());
            postItemDTO.setTitle(postDO.getTitle());
            postItemDTO.setCreatorName(idUserMap.get(postDO.getCreatorId()).getName());
            postItemDTO.setUpdateTime(postDO.getUpdateTime());
            postItemDTO.setReplyCount(replyCountMap.get(postDO.getId()).intValue());
            return postItemDTO;
        }).collect(Collectors.toList());
    }

    public Integer count(Integer topCount) {
        Integer count = postDAO.count();
        return count - topCount;
    }

    private List<Long> queryTopIdList() {
        ConfigDO configDO = configDAO.getByKey(Constants.TOP_POST_ID);
        if (configDO == null) return Lists.newArrayList();
        return DataConvert.convertValue2List(configDO.getValue());
    }
}
