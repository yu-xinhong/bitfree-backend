package com.jihai.bitfree.ability;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class MonitorAbility {

    private static String ROBOT_URL;

    private static final int MAX_MESSAGE_LENGTH = 150;

    @Autowired
    private ConfigService configService;

    @PostConstruct
    public void initRobotUrl() {
        ROBOT_URL = configService.getByKey(Constants.ROBOT_URL);
    }

    public void sendMsg(String message) {
        try {
            JSONObject postBodyJson = new JSONObject()
                    .fluentPut("msgtype", "text")
                    .fluentPut("text", new JSONObject().fluentPut("content", message.length() > MAX_MESSAGE_LENGTH ? message.substring(0, MAX_MESSAGE_LENGTH) + "......" : message));

            HttpResponse resp = new HttpRequest(ROBOT_URL).method(Method.POST).header("Content-Type", "application/json").timeout(1000).body(postBodyJson.toString()).execute();
            JSONObject response = JSON.parseObject(resp.body());
            if (! response.getString("errcode").equals("0")) {
                log.error("推送告警消息到机器人返回 {} 机器人返回异常信息 {}", postBodyJson.getInnerMap(), response.getString("errmsg"));
            }
        } catch (Exception e) {
            log.error("发送告警信息异常", e);
        }
    }
}
