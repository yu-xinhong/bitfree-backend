package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.UserDO;

import java.util.List;

public interface UserDAO {

    UserDO queryByEmailAndPassword(String email, String password);

    void saveToken(String email, String password, String token);

    void clearToken(Long id);

    UserDO getById(Long id);

    List<UserDO> batchQueryByIdList(List<Long> idList);
}
