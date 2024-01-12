package com.jihai.bitfree.service;

import com.jihai.bitfree.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserServiceTest {

    @Test
    @DisplayName("测试用户名校验规则")
    void checkName() {

        UserService us = new UserService();
        String[] names = new String[]{" 极海", " 极 海", " 极  海", " 极海 ", " 极 海 ", "极 海 ", "极-海", "-极-海", "-极-海-", "极海-"};
        for (String name : names){
            assertDoesNotThrow(() -> {
                try {
                    us.save(null, name, null, null, 1, null, 6L, 1L, null, 2);
                } catch (BusinessException ex) {
                    if (!"禁止使用该昵称".equals(ex.getMessage())) throw new BusinessException("Error");
                }
            }, "check name error: [" + name + "]");
        }
    }
}
