package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.OrderDO;

public interface OrderDAO {

    OrderDO getUserActivity(Long userId, Long activityId);

    void insert(OrderDO orderDO);

    OrderDO queryByUserIdAndActivityId(Long userId, Long activityId);

    int updateDetailById(Long id, String detail);
}
