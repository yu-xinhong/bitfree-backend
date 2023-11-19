package com.jihai.bitfree.unit;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class EmailRegexTest {

    @Test
    public void test() {
        ArrayList<String> mailList = Lists.newArrayList("123@gmail.com",
                "test_123@163.com",
                "test@outlook.com",
                "test@yeah.net",
                "tt.xxx.zz@qq.com",
                "xx87563xxx@163.com",
                "xxxx_1990@163.com",
                "runzez.reyes@outlook.com");

        String reg = "^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@([a-zA-Z0-9]+[-.])+(com|cn|edu|gov|net|org|vip|educ|ru)$";
        Pattern pattern = Pattern.compile(reg);
        mailList.forEach(mail -> Assert.assertTrue(mail + " not pass", pattern.matcher(mail).find()));
    }

}
