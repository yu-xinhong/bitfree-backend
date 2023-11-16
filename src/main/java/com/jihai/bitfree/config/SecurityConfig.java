package com.jihai.bitfree.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 安全配置类
 * @author : Immortal Chengge
 * @description : //TODO
 **/
@Configuration
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityConfig {

    /**
     * XSS攻击配置项
     */
    private XssConfig xss = new XssConfig(true);

}
