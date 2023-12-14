package com.jihai.bitfree.service;

import com.alibaba.fastjson.JSON;
import com.jihai.bitfree.bo.OrderInfoDetailBO;
import com.jihai.bitfree.dao.ActivityDAO;
import com.jihai.bitfree.dao.OrderDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.ActivityResp;
import com.jihai.bitfree.dto.resp.OrderResp;
import com.jihai.bitfree.entity.ActivityDO;
import com.jihai.bitfree.entity.OrderDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.strategy.Activity;
import com.jihai.bitfree.service.strategy.BaseActivityParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class ActivityService {

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private List<Activity> activityList;

    private ReentrantLock reentrantLock = new ReentrantLock(true);

    public ActivityResp getRecent() {
        ActivityDO activityDO = activityDAO.getRecent();
        if (activityDO == null) return null;
        ActivityResp target = new ActivityResp();
        BeanUtils.copyProperties(activityDO, target);
        return target;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean kill(Long userId, Long activityId) {
        ActivityDO activityDO = activityDAO.queryActivity(activityId);
        if (activityDO == null) {
            throw new BusinessException("活动不存在");
        }

        if (new Date().before(activityDO.getStartTime()) || new Date().after(activityDO.getEndTime())) {
            throw new BusinessException("请在活动时间内领取");
        }

        if (activityDO.getStock() <= 0) {
            throw new BusinessException("库存不足");
        }

        if (userDAO.getById(userId).getCoins() < activityDO.getCost()) {
            throw new BusinessException("硬币余额不足, 需要" + activityDO.getCost() + "硬币可参与活动");
        }
        // 重复扣减
        boolean sucLock = false;
        try {
            sucLock = reentrantLock.tryLock(1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new BusinessException("系统繁忙");
        }
        if (! sucLock) {
            throw new BusinessException("系统繁忙， 请稍后再试");
        }
        try {
            OrderDO userActivity = orderDAO.getUserActivity(userId, activityId);
            if (userActivity != null) {
                throw new BusinessException("重复参与活动");
            }

//            OrderDO orderDO = new OrderDO();
//            orderDO.setActivityId(activityId);
//            orderDO.setUserId(userId);
//            orderDO.setCount(1);

            /*boolean success = activityDAO.updateStock(activityId, 1) == 1;
            if (! success) {
                return false;
            }

            if (userDAO.incrementCoins(userId, - activityDO.getCost()) < 1) {
                throw new BusinessException("硬币余额不足");
            };*/

            activityList.forEach(activity -> activity.kill(new BaseActivityParam(userId, activityId, activityDO.getType())));
//            orderDAO.insert(orderDO);
        } finally {
            reentrantLock.unlock();
        }
        return true;
    }

    public Boolean submitInfo(Long userId, Long activityId, String name, String address, String size, String color, String tel) {
        OrderDO orderDO = orderDAO.queryByUserIdAndActivityId(userId, activityId);
        if (orderDO == null) {
            log.warn("some one try to submit invalid activity userId: {}, activityId: {}", userId, activityId);
            throw new BusinessException("没活动资格");
        }

        OrderInfoDetailBO orderInfoDetailBO = new OrderInfoDetailBO(name, address, tel, size, color);
        return orderDAO.updateDetailById(orderDO.getId(), JSON.toJSONString(orderInfoDetailBO)) == 1;
    }

    public Boolean getRight(Long userId, Long activityId) {
        ActivityDO activityDO = activityDAO.queryActivity(activityId);
        // 不在活动时间范围内
        if (activityDO.getStartTime().after(new Date()) || activityDO.getEndTime().before(new Date())) return false;
        // 库存为0
        if (activityDO.getStock() <= 0) return false;
        return orderDAO.getUserActivity(userId, activityId) == null;
    }

    public OrderResp getOrder(Long activityId, Long userId) {
        OrderDO orderDO = orderDAO.getUserActivity(userId, activityId);
        if (Objects.isNull(orderDO)) return null;

        OrderResp orderResp = new OrderResp();
        BeanUtils.copyProperties(orderDO, orderResp);
        return orderResp;
    }
}
