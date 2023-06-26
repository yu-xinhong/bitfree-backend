package com.jihai.bitfree.service;


import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.dao.OperateLogDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.ActivityUserResp;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.entity.OperateLogDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.utils.DO2DTOConvert;
import com.jihai.bitfree.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
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

    public String addUser(String email, Integer level, String secret) {

        checkSecret(secret);
        // 校验
        if (userDao.queryByEmail(email) != null) {
            log.error("email {} is duplicated", email);
            throw new RuntimeException("邮箱重复创建");
        }

        UserDO userDO = new UserDO();
        userDO.setEmail(email);

        String password = PasswordUtils.generatePwd();
        userDO.setPassword(PasswordUtils.md5(password));

        userDO.setLevel(level);

        userDao.insert(userDO);

        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setUserId(userDO.getId());
        operateLogDO.setType(OperateTypeEnum.INIT_USER.getCode());

        operateLogDAO.insert(operateLogDO);
        return password;
    }

    private void checkSecret(String secret) {
        ConfigDO configDO = configDAO.getByKey(Constants.SECRET);
        if (! configDO.getValue().equals(secret)) {
            log.error("warn ! addUser is only allow administrator jihai!");
            // send alert
            throw new RuntimeException(Constants.NOT_LOGIN);
        }
    }

    public UserResp getByToken(String token) {
        return DO2DTOConvert.convertUser(userDao.getByToken(token));
    }

    @Transactional
    public Boolean save(String avatar, String name, String city, String position, String seniority, Long userId) {
        userDao.save(userId, avatar, name, city, position, seniority);
        return true;
    }

    public Boolean hadModifyPwd(Long id) {
        return operateLogDAO.queryByUserIdAndType(id, OperateTypeEnum.UPDATE_PASSWORD.getCode()) > 0;
    }

    public List<ActivityUserResp> getActivityList() {
        return userDao.ActivityUserResp();
    }

    public Boolean resetPassword(Long id, String secret, String defaultPassword) {
        checkSecret(secret);
        userDao.updatePasswordAndClearToken(id, defaultPassword);
        return true;
    }

    public Boolean updatePassword(Long id, String oldPassword, String newPassword) {
        if (StringUtils.hasText(newPassword)) {
            UserDO userDO = userDao.getById(id);
            if (! userDO.getPassword().equalsIgnoreCase(oldPassword)) {
                log.warn("some one password new and old not equals");
                throw new RuntimeException(ReturnCodeEnum.USER_OLD_PASSWORD_ERROR.getDesc());
            }

            if (userDO.getPassword().equalsIgnoreCase(newPassword)) {
                throw new RuntimeException(ReturnCodeEnum.SAME_PASSWORD_ERROR.getDesc());
            }
        }
        userDao.updatePasswordAndClearToken(id, newPassword);

        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setUserId(id);
        operateLogDO.setType(OperateTypeEnum.UPDATE_PASSWORD.getCode());

        operateLogDAO.insert(operateLogDO);

        return true;
    }
}
