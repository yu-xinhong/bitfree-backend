package com.jihai.bitfree.unit;


import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.controller.TopicController;
import com.jihai.bitfree.dto.resp.TopicDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.Cookie;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AppTest {

    @Autowired
    private TopicController topicController;


    @Before
    public void mockHttpRequest() {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setCookies(new Cookie("token", "thisIsMyToken"));
        topicController.httpServletRequest = httpServletRequest;
    }

    @Test
    public void queryAllTopics() {
        Result<List<TopicDTO>> allTopic = topicController.getAllTopic();
        Assert.assertTrue(allTopic.getData().size() > 0);
    }
}
