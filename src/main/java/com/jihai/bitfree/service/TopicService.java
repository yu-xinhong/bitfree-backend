package com.jihai.bitfree.service;

import com.google.common.collect.Lists;
import com.jihai.bitfree.dao.TopicDAO;
import com.jihai.bitfree.dto.resp.TopicResp;
import com.jihai.bitfree.entity.TopicDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    @Autowired
    private TopicDAO topicDAO;

    public List<TopicResp> getAllTopic() {
        List<TopicDO> topicDOList = topicDAO.getAllTopic();
        if (CollectionUtils.isEmpty(topicDOList)) return Lists.newArrayList();

        return topicDOList.stream().map(topicDO -> {
            TopicResp topicResp = new TopicResp();
            topicResp.setId(topicDO.getId());
            topicResp.setName(topicDO.getName());
            return topicResp;
        }).collect(Collectors.toList());
    }
}
