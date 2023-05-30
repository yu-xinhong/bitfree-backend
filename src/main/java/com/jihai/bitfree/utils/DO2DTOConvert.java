package com.jihai.bitfree.utils;

import com.jihai.bitfree.dto.resp.UserDTO;
import com.jihai.bitfree.entity.UserDO;
import org.springframework.beans.BeanUtils;

public class DO2DTOConvert {
    public static UserDTO convertUser(UserDO user) {
        UserDTO target = new UserDTO();
        BeanUtils.copyProperties(user, target);
        return target;
    }
}
