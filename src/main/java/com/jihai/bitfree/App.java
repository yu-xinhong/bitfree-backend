package com.jihai.bitfree;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.event.EventListener;
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

    @EventListener( ApplicationReadyEvent.class )
    public void ready() {
        Logger logger = LoggerFactory.getLogger( App.class );
        logger.info( "\uD83D\uDE80\uD83D\uDE80\uD83D\uDE80 启动成功，欢迎加入bitfree" );
    }
}
