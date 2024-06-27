package com.jihai.bitfree.upload;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class UploadTypeInitializer {

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        // 获取所有实现了UploadTypeRegistrable接口的bean
        Map<String, UploadTypeRegistrable> beans
                = applicationContext.getBeansOfType(UploadTypeRegistrable.class);

        // 调用每个bean的registry方法
        beans.values().forEach(UploadTypeRegistrable::registry);
    }
}
