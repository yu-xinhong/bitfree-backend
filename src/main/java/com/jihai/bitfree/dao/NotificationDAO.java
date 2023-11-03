package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.NotificationDO;

import java.util.List;

public interface NotificationDAO {

    List<NotificationDO> pageQuery(Integer start, Integer size, Integer type);

    Integer total();

    NotificationDO detail(Long id);

    List<NotificationDO> getAll();

    NotificationDO getById(Long id);

    int updateUserIdListById(Long id, String userList);
}
