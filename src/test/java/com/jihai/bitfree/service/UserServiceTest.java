package com.jihai.bitfree.service;

import com.jihai.bitfree.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    @Test
    @DisplayName("测试用户名校验规则")
    void checkName() {

        UserService us = new UserService();
        assertThrows(BusinessException.class, () -> us.save(null, " 极海", null, null, 1, null, 6L, 1L, null, 2));
        assertThrows(BusinessException.class, () -> us.save(null, " 极 海", null, null, 1, null, 6L, 1L, null, 2));
        assertThrows(BusinessException.class, () -> us.save(null, " 极  海", null, null, 1, null, 6L, 1L, null, 2));
        assertThrows(BusinessException.class, () -> us.save(null, " 极海 ", null, null, 1, null, 6L, 1L, null, 2));
        assertThrows(BusinessException.class, () -> us.save(null, " 极 海 ", null, null, 1, null, 6L, 1L, null, 2));
        assertThrows(BusinessException.class, () -> us.save(null, "极 海 ", null, null, 1, null, 6L, 1L, null, 2));

    }
}
