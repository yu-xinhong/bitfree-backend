package com.jihai.bitfree.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jihai.bitfree.ability.ThreadPoolAbility;
import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.dao.OperateLogDAO;
import com.jihai.bitfree.dto.resp.WebStaticsResp;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.entity.OperateLogDO;
import com.jihai.bitfree.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class StatisticService {

    @Autowired
    private ConfigDAO configDAO;

    @Autowired
    private OperateLogDAO operateLogDAO;

    @Autowired
    private ThreadPoolAbility threadPoolAbility;

    @PostConstruct
    public void init() {
        ConfigDO config = configDAO.getByKey(Constants.WEB_STATISTICS);
        if (config == null) {
            // 第一次发布初始化代码
            WebStaticsResp webStaticsResp = new WebStaticsResp();
            webStaticsResp.setRequestCount(0);
            webStaticsResp.setUserLoginCount(0);

            config = new ConfigDO();
            config.setKey(Constants.WEB_STATISTICS);
            config.setValue(JSONObject.toJSONString(webStaticsResp));
            configDAO.insert(config);
        }

        new Thread(() -> {
            while (true) {
                // 每5分钟同步一次DB即可
                try {
                    Thread.sleep(1000 * 60 * 5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                WebStaticsResp webStaticsResp = webStatistics();
                configDAO.updateKey(Constants.WEB_STATISTICS, JSONObject.toJSONString(webStaticsResp));
            }
        }).start();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Async("statisticThreadPool")
    public void clear() {
        // 每天0点清除
        log.info("start clear web statistic {}", webStatistics());
        clearStatistics();
    }

    private void clearStatistics() {
        WebStaticsResp webStaticsResp = new WebStaticsResp();
        webStaticsResp.setRequestCount(0);
        webStaticsResp.setUserLoginCount(0);

        configDAO.updateKey(Constants.WEB_STATISTICS, JSONObject.toJSONString(webStaticsResp));
        statisticCache.invalidate(Constants.WEB_STATISTICS);
    }

    // TODO 定期刷新缓存即可
    private Cache<String, WebStaticsResp> statisticCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    public WebStaticsResp webStatistics() {
        try {
            return statisticCache.get(Constants.WEB_STATISTICS, () -> {
                ConfigDO config = configDAO.getByKey(Constants.WEB_STATISTICS);
                if (config == null) return null;
                String value = config.getValue();
                return JSONObject.parseObject(value, WebStaticsResp.class);
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("statisticThreadPool")
    public void recordRequest() {
        WebStaticsResp webStaticsResp = webStatistics();
        webStaticsResp.setRequestCount(webStaticsResp.getRequestCount() + 1);
    }

    @Async("statisticThreadPool")
    public void recordUserLog(Long userId) {
        // 上层单机单线程不存在并发
        try {
            Date date = DateUtils.formatDay(new Date());
            if (operateLogDAO.countLoginRecord(userId, OperateTypeEnum.LOGIN.getCode(), date) > 0) {
                return ;
            }

            OperateLogDO operateLogDO = new OperateLogDO();
            operateLogDO.setUserId(userId);
            operateLogDO.setType(OperateTypeEnum.LOGIN.getCode());
            operateLogDAO.insert(operateLogDO);

            WebStaticsResp webStaticsResp = webStatistics();
            webStaticsResp.setUserLoginCount(webStaticsResp.getUserLoginCount() + 1);
        } catch (Exception e) {
            log.error("record user login error", e);
        }
    }
}
