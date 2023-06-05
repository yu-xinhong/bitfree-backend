package com.jihai.bitfree.dao;

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

    UserDO getByToken(@Param("token") String token);

    UserDO queryByEmail(String email);

    void save(@Param("userId") Long userId, @Param("name") String name, @Param("city") String city, @Param("position") String position,@Param("seniority") String seniority,@Param("password") String password);
}
