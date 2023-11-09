package com.jihai.bitfree.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;

@Component
public class DataSourceConfigLoader implements BeanPostProcessor, EnvironmentAware {

    private ConfigurableEnvironment environment;

    @Value("${datasource.config-path}")
    private String configPath;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MybatisAutoConfiguration) {
            String config = FileUtils.readFileToString(new File(configPath + "/db.json"), Charset.defaultCharset());
            JSONObject configJson = JSON.parseObject(config);
            environment.getSystemProperties().put("spring.datasource.type", "com.zaxxer.hikari.HikariDataSource");
            environment.getSystemProperties().put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
            environment.getSystemProperties().put("spring.datasource.url", configJson.getString("url"));
            environment.getSystemProperties().put("spring.datasource.password", configJson.getString("password"));
            environment.getSystemProperties().put("spring.datasource.username", configJson.getString("username"));
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }


    /**
     * spring:
     *   # MYSQL data source configuration. No need to explain too much.
     *   datasource:
     *     type: com.zaxxer.hikari.HikariDataSource
     *     driver-class-name: com.mysql.jdbc.Driver
     *     url: jdbc:mysql://127.0.0.1:3306/bitfree?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true
     * #    username:
     * #    password:
     * @return
     */





   /* @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        String config;
        try {
            config = FileUtils.readFileToString(new File(configPath + "/db.json"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONObject configJson = JSON.parseObject(config);


        DataSourceProperties dataSourceProperties = new DataSourceProperties();

        dataSourceProperties.setType(com.zaxxer.hikari.HikariDataSource.class);
        dataSourceProperties.setDriverClassName("com.mysql.jdbc.Driver");

        dataSourceProperties.setDataPassword(configJson.getString("password"));
        dataSourceProperties.setDataUsername(configJson.getString("username"));
        dataSourceProperties.setUrl(configJson.getString("url"));

        environment.getSystemEnvironment().put("spring.datasource.type", "com.zaxxer.hikari.HikariDataSource");
        environment.getSystemEnvironment().put("spring.datasource.driver-class-name", "com.zaxxer.hikari.HikariDataSource");
        environment.getSystemEnvironment().put("spring.datasource.url", configJson.getString("url"));
        environment.getSystemEnvironment().put("spring.datasource.password", configJson.getString("password"));
        environment.getSystemEnvironment().put("spring.datasource.username", configJson.getString("username"));
    }*/


}
