package com.jihai.bitfree.service;


import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.constants.OperateTypeEnum;
import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.dao.OperateLogDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.UserDTO;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.entity.OperateLogDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserDAO userDao;

    @Autowired
    private ConfigDAO configDAO;

    @Autowired
    private OperateLogDAO operateLogDAO;

    public UserDO queryByEmailAndPassword(String email, String password) {
        return userDao.queryByEmailAndPassword(email, password);
    }

    public String generateToken(String email, String password) {
        String token = UUID.randomUUID().toString();
        userDao.saveToken(email, password, token);
        return token;
    }

    public void logout(Long id) {
        userDao.clearToken(id);
    }

    public UserDO getUser(Long id) {
        return userDao.getById(id);
    }

    public String addUser(String email, String secret) {
        ConfigDO configDO = configDAO.getByKey(Constants.SECRET);
        if (! configDO.getValue().equals(secret)) {
            log.error("warn ! addUser is only allow administrator jihai!");
            // send alert
            return "false";
        }

        UserDO userDO = new UserDO();
        userDO.setEmail(email);

        String password = PasswordUtils.generatePwd();
        userDO.setPassword(password);

        userDao.insert(userDO);

        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setUserId(userDO.getId());
        operateLogDO.setType(OperateTypeEnum.INIT_USER.getCode());

        operateLogDAO.insert(operateLogDO);
        return userDO.getPassword();
    }

    public UserDTO getByToken(String token) {
        return userDao.getByToken(token);
    }
}
