package com.jihai.bitfree;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.jihai.bitfree.dao"})
public class App {

    public static void main(String[] args) {
        new SpringApplication(App.class).run(args);
    }
}
