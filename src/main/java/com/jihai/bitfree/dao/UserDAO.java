package com.jihai.bitfree.dao;

import com.jihai.bitfree.dto.resp.ActivityUserResp;
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

    void save(@Param("userId") Long userId,@Param("avatar") String avatar, @Param("name") String name, @Param("city") String city, @Param("position") String position, @Param("seniority") Integer seniority);

    List<ActivityUserResp> ActivityUserResp();

    void updatePasswordAndClearToken(@Param("id") Long id, @Param("password") String password);

    void incrementCoins(@Param("userId") Long userId, @Param("coins") Integer coins);

    List<Long> listAllUserId();

}
