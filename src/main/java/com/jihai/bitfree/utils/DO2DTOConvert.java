package com.jihai.bitfree.utils;

import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import org.springframework.beans.BeanUtils;

public class DO2DTOConvert {
    public static UserResp convertUser(UserDO user) {
        if (user == null) throw new BusinessException("禁止注入扫描");
        UserResp target = new UserResp();
        BeanUtils.copyProperties(user, target);
        return target;
    }
}
