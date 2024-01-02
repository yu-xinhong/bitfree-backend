package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.dao.ActivityDAO;
import com.jihai.bitfree.dao.OrderDAO;
import com.jihai.bitfree.dto.resp.OrderResp;
import com.jihai.bitfree.entity.ActivityDO;
import com.jihai.bitfree.entity.OrderDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author McDull
 */
@Service
public class OrderService {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private ActivityDAO activityDAO;

    public PageResult<OrderResp> pageUserOrder(long userId, int page, int size) {
        List<OrderDO> orders = orderDAO.pageByUser(userId, (page - 1) * size, size);
        if(CollectionUtils.isEmpty(orders)) return new PageResult<>(Collections.emptyList(), 0);

        Set<Long> activities = orders.stream()
            .map(OrderDO::getActivityId)
            .collect(Collectors.toSet());
        List<ActivityDO> activityDOS = activityDAO.queryByIds(activities);
        ImmutableMap<Long, ActivityDO> activityMap = Maps.uniqueIndex(activityDOS, ActivityDO::getId);

        List<OrderResp> orderResps = orders.stream()
            .map(order -> {
                ActivityDO activity = activityMap.get(order.getActivityId());
                OrderResp orderResp = new OrderResp();
                BeanUtils.copyProperties(order, orderResp);
                orderResp.setActivityName(activity.getName());
                return orderResp;
            })
            .sorted(Comparator.comparing(OrderResp::getCreateTime)
                .reversed())
            .collect(Collectors.toList());
        int total = orderDAO.countByUser(userId);
        return new PageResult<>(orderResps, total);
    }
}
