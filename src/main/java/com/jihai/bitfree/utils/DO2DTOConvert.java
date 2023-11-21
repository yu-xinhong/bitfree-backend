package com.jihai.bitfree.utils;

import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DO2DTOConvert {
    public static UserResp convertUser(UserDO user) {
        if (user == null) throw new BusinessException("禁止注入扫描");
        UserResp target = new UserResp();
        BeanUtils.copyProperties(user, target);
        return target;
    }

    public static List<UserResp> convertUsers(List<UserDO> user) {
        if (CollectionUtils.isEmpty(user)) {
            return Collections.EMPTY_LIST;
        }
        List<UserResp> target = new ArrayList<>();
        for (UserDO userDO : user) {
            target.add(convertUser(userDO));
        }
        return target;
    }
}
