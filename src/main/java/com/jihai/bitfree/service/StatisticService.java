package com.jihai.bitfree.service;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
@Slf4j
public class StatisticService {

    @Autowired
    private ConfigDAO configDAO;

    @Autowired
    private OperateLogDAO operateLogDAO;

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
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Async("commonAsyncThreadPool")
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
    }


    public WebStaticsResp webStatistics() {
        ConfigDO config = configDAO.getByKey(Constants.WEB_STATISTICS);
        if (config == null) return null;
        String value = config.getValue();
        return JSONObject.parseObject(value, WebStaticsResp.class);
    }

    @Async("commonAsyncThreadPool")
    public void recordRequest() {
        WebStaticsResp webStaticsResp = webStatistics();
        webStaticsResp.setRequestCount(webStaticsResp.getRequestCount() + 1);

        configDAO.updateKey(Constants.WEB_STATISTICS, JSONObject.toJSONString(webStaticsResp));
    }

    @Async("commonAsyncThreadPool")
    @Transactional
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
            configDAO.updateKey(Constants.WEB_STATISTICS, JSONObject.toJSONString(webStaticsResp));
        } catch (Exception e) {
            log.error("record user login error", e);
        }
    }
}
