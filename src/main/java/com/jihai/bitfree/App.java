package com.jihai.bitfree;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = {"com.jihai.bitfree.dao"})
@EnableScheduling
@EnableAsync
@ServletComponentScan
public class App {

    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);
    }
}
