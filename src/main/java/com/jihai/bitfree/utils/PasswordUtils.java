package com.jihai.bitfree.utils;

import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.UUID;

@Component
public class PasswordUtils {

    @Autowired
    private ConfigDAO configDAO;

    public static String generatePwd() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public static String md5(String password) {
        try {
            byte[] bytes = password.getBytes("UTF-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            return DatatypeConverter.printHexBinary(md5.digest());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String defaultPassword() {
        ConfigDO configDO = configDAO.getByKey(Constants.DEFAULT_PASSWORD_KEY);
        if (configDO == null) {
            throw new BusinessException("未配置默认密码");
        }
        return md5(configDO.getValue());
    }
}
