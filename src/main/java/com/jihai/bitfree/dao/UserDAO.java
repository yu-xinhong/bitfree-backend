package com.jihai.bitfree.dao;

import com.jihai.bitfree.dto.resp.UserDTO;
import com.jihai.bitfree.entity.UserDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDAO {

    UserDO queryByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    void saveToken(@Param("email") String email, @Param("password") String password, @Param("token") String token);

    void clearToken(@Param("id") Long id);

    UserDO getById(@Param("id") Long id);

    List<UserDO> batchQueryByIdList(@Param("idList") List<Long> idList);

    void insert(UserDO userDO);

    UserDTO getByToken(@Param("token") String token);
}
