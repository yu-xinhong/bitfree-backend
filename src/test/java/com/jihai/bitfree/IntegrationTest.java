package com.jihai.bitfree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author zhan yan
 **/
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class IntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    DataSource datasource;



}
