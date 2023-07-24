package com.jihai.bitfree.unit;


import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.controller.TopicController;
import com.jihai.bitfree.dto.resp.TopicResp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    private ApplicationContext applicationContext;


    @Test
    public void queryAllTopics() {
        Result<List<TopicResp>> allTopic = topicController.getAllTopic();
        Assert.assertTrue(allTopic.getData().size() > 0);

//        AopProxy aopProxy = ((AopProxy) applicationContext.getBean(TopicController.class));
//        System.out.println(aopProxy);;

        System.out.println(topicController.getAllTopic());
        TopicController singletonTarget = (TopicController)AopProxyUtils.getSingletonTarget(topicController);
        System.out.println(singletonTarget.getAllTopic());
    }
}
