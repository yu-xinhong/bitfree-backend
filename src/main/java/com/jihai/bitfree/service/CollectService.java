package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jihai.bitfree.dao.CollectDAO;
import com.jihai.bitfree.dao.PostDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.CollectResp;
import com.jihai.bitfree.entity.CollectDO;
import com.jihai.bitfree.entity.PostDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CollectService {

    @Autowired
    private CollectDAO collectDAO;

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private UserDAO userDAO;

    public List<CollectResp> getCollectList(Long userId, Integer page, Integer size) {
        List<CollectDO> collectDOList = collectDAO.pageQuery(userId, (page - 1) * size, size, CollectTypeEnum.POST.getType());

        if (CollectionUtils.isEmpty(collectDOList)) return Collections.emptyList();

        List<Long> postIdList = collectDOList.stream().map(e -> e.getTargetId()).distinct().collect(Collectors.toList());
        //  这里获取的postDOList可能为空(postIdList中的贴子都被删除)
        //  即使postDOList为空, Maps#uniqueIndex也不会报错
        List<PostDO> postDOList = postDAO.getByIdList(postIdList);
        if (CollectionUtils.isEmpty(postDOList)) return Collections.emptyList();
        ImmutableMap<Long, PostDO> postIdMap = Maps.uniqueIndex(postDOList, PostDO::getId);

        List<Long> userIdList = postDOList.stream().map(e -> e.getCreatorId()).distinct().collect(Collectors.toList());
        //  当postDOList为空时userIdList也为空, batchQueryByIdList调用会语法异常
        List<UserDO> userDOList = userDAO.batchQueryByIdList(userIdList);
        ImmutableMap<Long, UserDO> userIdMap = Maps.uniqueIndex(userDOList, UserDO::getId);

        return collectDOList.stream()
                //  postIdMap中不会有被删除的帖子, 而收藏栏中仍会有被删除的帖子, 需要过滤
                .filter(collect -> postIdMap.containsKey(collect.getTargetId()))
                .map(e -> {
                    CollectResp collectResp = new CollectResp();
                    collectResp.setTitle(postIdMap.get(e.getTargetId()).getTitle());
                    collectResp.setCreatorName(userIdMap.get(postIdMap.get(e.getTargetId()).getCreatorId()).getName());
                    collectResp.setCreateTime(postIdMap.get(e.getTargetId()).getCreateTime());
                    collectResp.setPostId(e.getTargetId());
                    return collectResp;
                }).collect(Collectors.toList());

    }

    public Boolean cancelCollect(Long postId, Long id, Integer type) {
        if (collectDAO.hasCollect(postId, id, type) == 0) {
            log.error("不存在收藏帖子，无法取消，postId:{}, userId:{}", postId, id);
            throw new BusinessException("不存在收藏");
        }
        // 非核心表, 直接硬删除
        return collectDAO.delete(postId, id, type) > 0;
    }

    public Boolean collect(Long postId, Long userId, Integer type) {
        if (collectDAO.hasCollect(postId, userId, type) > 0) {
            return true;
        }
        // 并发暂时由 DB UK兜底
        CollectDO collectDO = new CollectDO();
        collectDO.setTargetId(postId);
        collectDO.setType(CollectTypeEnum.POST.getType());
        collectDO.setUserId(userId);

        collectDAO.insert(collectDO);
        return true;
    }

    public Integer countTotal(Long userId) {
        return collectDAO.countTotal(userId, CollectTypeEnum.POST.getType());
    }

    public Boolean hasCollected(Long postId, Long userId) {
        return collectDAO.hasCollect(postId, userId, CollectTypeEnum.POST.getType()) > 0;
    }

    public enum CollectTypeEnum {
        POST(1, "帖子");
        private Integer type;
        private String desc;

        CollectTypeEnum(Integer type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public Integer getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }

    }
}
