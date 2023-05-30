package com.jihai.bitfree.unit;

import com.jihai.bitfree.service.PostService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceUnit extends AppTest{


    @Autowired
    private PostService postService;

    @Test
    public void initPostList() {
        for (int i = 0; i < 200; i++) {
            postService.add("标题" + i,
                    "这是测试内容这是测试内容这是测试内容这是测试内容这是测试内容这是测试内容这是测试内容这是测试内容这是测试内容",
                    1, 14L);
        }
    }
}
