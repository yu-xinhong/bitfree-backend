package com.jihai.bitfree.service;


import com.jihai.bitfree.base.enums.LikeTypeEnum;
import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.constants.CoinsDefinitions;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.*;
import com.jihai.bitfree.dto.resp.ActivityUserResp;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.*;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.utils.DO2DTOConvert;
import com.jihai.bitfree.utils.DateUtils;
import com.jihai.bitfree.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private static final String DEFAULT_AVATAR = "DEFAULT_AVATAR";
    @Autowired
    private UserDAO userDao;

    @Autowired
    private ConfigDAO configDAO;

    @Autowired
    private OperateLogDAO operateLogDAO;

    @Autowired
    private CheckInDAO checkInDAO;

    @Autowired
    private UserLikeDAO userLikeDAO;

    @Autowired
    private ReplyDAO replyDAO;

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
        userDO.setAvatar(configDAO.getByKey(DEFAULT_AVATAR).getValue());
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
    public Boolean save(String avatar, String name, String city, String position, Integer seniority, Long userId) {
        if (! StringUtils.isEmpty(avatar) && avatar.contains("jihai")) throw new BusinessException("禁止使用该头像");
        if (! StringUtils.isEmpty(name) && name.contains("极海")) throw new BusinessException("禁止使用该昵称");
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

    public Boolean getCheckIn(Long userId) {
        return checkInDAO.getByCurrentDay(userId, DateUtils.formatDay(new Date())) > 0;
    }

    @Transactional
    public Boolean checkIn(Long userId) {
        if (getCheckIn(userId)) throw new BusinessException("重复签到");

        checkInDAO.insert(userId, DateUtils.formatDay(new Date()));
        userDao.incrementCoins(userId, CoinsDefinitions.CHECK_IN);
        return true;
    }

    public Boolean like(Long id, Integer type, Boolean like, Long userId) {
        UserLikeDO userLikeDO = new UserLikeDO();
        userLikeDO.setTargetId(id);
        userLikeDO.setType(type);
        userLikeDO.setUserId(userId);
        userLikeDO.setValue(like);

        userLikeDAO.insert(userLikeDO);

        // 给目标用户添加硬币
        if (type == LikeTypeEnum.REPLY.getType()) {
            // 添加一个1个币
            ReplyDO replyDO = replyDAO.getById(id);
            Long sendUserId = replyDO.getSendUserId();
            userDao.incrementCoins(sendUserId, 1);
        }
        return true;
    }
}
