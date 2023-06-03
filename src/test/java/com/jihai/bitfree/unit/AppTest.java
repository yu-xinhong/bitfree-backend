package com.jihai.bitfree.unit;


import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.controller.TopicController;
import com.jihai.bitfree.dto.resp.TopicResp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
//@Transactional
public class AppTest {

    @Autowired
    private TopicController topicController;


    @Test
    public void queryAllTopics() {
        Result<List<TopicResp>> allTopic = topicController.getAllTopic();
        Assert.assertTrue(allTopic.getData().size() > 0);
    }
}
