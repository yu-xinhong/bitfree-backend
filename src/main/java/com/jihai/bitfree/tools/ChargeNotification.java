package com.jihai.bitfree.tools;

import cn.hutool.http.GlobalHeaders;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.jihai.bitfree.ability.MonitorAbility;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 余额提示
 */
@Component
@Slf4j
public class ChargeNotification {

    @Autowired
    private ConfigService configService;

    @Autowired
    private MonitorAbility monitorAbility;

    @PostConstruct
    public void notification() {
        new Thread(() -> {
            while (true) {
                // 伪装请求频率，避免被识别
                try {
                    Thread.sleep(RandomUtils.nextInt(1, 10) * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    // 获取网站配置
                    ChargeUrlConfig chargeUrlConfig = getChargeConfig();
                    // 获取到余额
                    Double balance = executeAndGetBalance(chargeUrlConfig);
                    // 发送告警
                    sendNotification(balance, chargeUrlConfig.getAmount());
                } catch (Exception e) {
                    log.warn("charge query error " + e.getMessage());
                }
            }
        }).start();
    }

    private void sendNotification(Double balance, Double amount) {
        if (balance >= amount) return ;
        monitorAbility.sendMsg("电费余额仅剩: " + balance);
    }

    private Double executeAndGetBalance(ChargeUrlConfig chargeUrlConfig) {
        try {
            GlobalHeaders.INSTANCE.header("Cookie", chargeUrlConfig.getCookie());
            String resp = HttpUtil.get(chargeUrlConfig.getUrl());
            if (resp.equals("Error request, response status: 502")) throw new BusinessException("http status 502");
            Document document = Jsoup.parse(resp);
            Elements table = document.getElementsByTag("table");
            return Double.valueOf(table.get(0).childNodes().get(1).childNodes().get(1).childNodes().get(4).childNodes().get(0).toString());
        } catch (Exception e) {
            throw new BusinessException("请求 " + chargeUrlConfig.getUrl() + " 网站异常");
        }
    }

    private ChargeUrlConfig getChargeConfig() {
        return JSON.parseObject(configService.getByKey(Constants.CHARGE_CONFIG), ChargeUrlConfig.class);
    }

    public static class ChargeUrlConfig {

        private String cookie;

        private String url;

        private Double amount;

        public String getCookie() {
            return cookie;
        }

        public void setCookie(String cookie) {
            this.cookie = cookie;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}
