package com.jihai.bitfree.utils;

import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.UserDO;
import org.springframework.beans.BeanUtils;

public class DO2DTOConvert {
    public static UserResp convertUser(UserDO user) {
        UserResp target = new UserResp();
        BeanUtils.copyProperties(user, target);
        return target;
    }
}
