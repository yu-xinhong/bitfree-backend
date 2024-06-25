package com.jihai.bitfree.upload;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassPathUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("local")
public class UploadTest {

    @Autowired
    UploadFacade uploadFacade;

    @Test
    public void testUpload() {
        // 读取 class path 下的文件
        ClassPathResource classPathResource = new ClassPathResource("pic/test.jpg");
        File file = FileUtil.file(classPathResource.getPath());
        String linkUrl = uploadFacade.upload(file);
        log.info("linkUrl: {}", linkUrl);
    }

}
