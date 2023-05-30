package com.jihai.bitfree.unit;


import com.jihai.bitfree.controller.PostController;
import com.jihai.bitfree.controller.TopicController;
import com.jihai.bitfree.controller.UserController;
import com.jihai.bitfree.dto.req.AddPostReq;
import com.jihai.bitfree.dto.req.AddUserReq;
import com.jihai.bitfree.dto.req.PageQueryReq;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.Cookie;
import java.util.Random;

public class MockDataUtils extends AppTest {

    @Autowired
    private UserController userController;

    @Autowired
    private PostController postController;

    @Autowired
    private TopicController topicController;

    @Test
    public void initUser() {
        for (int i = 0; i < 10; i++) {
            AddUserReq addUserReq = new AddUserReq();
            addUserReq.setEmail("email_" + i + "@.com");
            addUserReq.setSecret("jihai-engineer");
            userController.addUser(addUserReq);
        }
    }


    @Test
    public void addPost() {
        Random random = new Random();

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.setCookies(new Cookie("token", "thisIsMyToken"));
        postController.httpServletRequest = httpServletRequest;

        for (int i = 0; i < 100; i++) {
            AddPostReq addPostReq = new AddPostReq();
            addPostReq.setContent("<p>这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章这是文章</p>");
            addPostReq.setTitle("这是标题" + i);
            addPostReq.setTopicId(random.nextInt(4));
            postController.add(addPostReq);
        }


        PageQueryReq pageQueryReq = new PageQueryReq();
        postController.pageQuery(pageQueryReq);
    }

}
