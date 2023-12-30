package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.OrderDO;

import java.util.List;

public interface OrderDAO {

    OrderDO getUserActivity(Long userId, Long activityId);

    void insert(OrderDO orderDO);

    OrderDO queryByUserIdAndActivityId(Long userId, Long activityId);

    int updateDetailById(Long id, String detail);

    List<OrderDO> pageByUser(long userId, int start, int size);
}
