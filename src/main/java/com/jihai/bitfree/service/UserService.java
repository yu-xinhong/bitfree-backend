package com.jihai.bitfree.service;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jihai.bitfree.ability.MonitorAbility;
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
import com.jihai.bitfree.lock.DistributedLock;
import com.jihai.bitfree.utils.DO2DTOConvert;
import com.jihai.bitfree.utils.DateUtils;
import com.jihai.bitfree.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {

    private static final String DEFAULT_AVATAR = "DEFAULT_AVATAR";
    @Autowired
    private UserDAO userDAO;

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

    @Autowired
    private PostDAO postDAO;

    @Autowired
    private MonitorAbility monitorAbility;

    public UserDO queryByEmailAndPassword(String email, String password) {
        return userDAO.queryByEmailAndPassword(email, password);
    }

    public String generateToken(String email, String password, String currentIp) {
        String token = UUID.randomUUID().toString();
        userDAO.saveToken(email, password, token, currentIp);
        return token;
    }

    public void logout(Long id) {
        userDAO.clearToken(id);
    }

    public UserDO getUser(Long id) {
        return userDAO.getById(id);
    }

    public String addUser(String email, Integer level, String secret) {

        checkSecret(secret);
        // 校验
        if (userDAO.queryByEmail(email) != null) {
            log.error("email {} is duplicated", email);
            throw new RuntimeException("邮箱重复创建");
        }

        UserDO userDO = new UserDO();
        userDO.setAvatar(configDAO.getByKey(DEFAULT_AVATAR).getValue());
        userDO.setEmail(email);

        String password = PasswordUtils.generatePwd();
        userDO.setPassword(PasswordUtils.md5(password));

        userDO.setLevel(level);

        userDAO.insert(userDO);

        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setUserId(userDO.getId());
        operateLogDO.setType(OperateTypeEnum.INIT_USER.getCode());

        operateLogDAO.insert(operateLogDO);
        return password;
    }

    private void checkSecret(String secret) {
        ConfigDO configDO = configDAO.getByKey(Constants.SECRET);
        if (! configDO.getValue().equals(secret)) {
            log.error("warn ! this operation only be allowed to administrator jihai!");
            // send alert
            throw new RuntimeException(ReturnCodeEnum.SECRET_ERROR.getDesc());
        }
    }

    public UserResp getByToken(String token) {
        return DO2DTOConvert.convertUser(userDAO.getByToken(token));
    }

    @Transactional
    public Boolean save(String avatar, String name, String city, String position, Integer seniority, Long userId) {
        if (! StringUtils.isEmpty(avatar) && avatar.contains("jihai")) throw new BusinessException("禁止使用该头像");
        if (! StringUtils.isEmpty(name) && name.contains("极海")) throw new BusinessException("禁止使用该昵称");
        userDAO.save(userId, avatar, name, city, position, seniority);
        return true;
    }

    public Boolean hadModifyPwd(Long id) {
        return operateLogDAO.queryByUserIdAndType(id, OperateTypeEnum.UPDATE_PASSWORD.getCode()) > 0;
    }

    public List<ActivityUserResp> getActivityList() {
        return userDAO.ActivityUserResp();
    }

    public Boolean resetPassword(Long id, String secret, String defaultPassword) {
        checkSecret(secret);
        userDAO.updatePasswordAndClearToken(id, defaultPassword);
        return true;
    }

    @Transactional
    public Boolean updatePassword(Long id, String oldPassword, String newPassword) {
        if (StringUtils.hasText(newPassword)) {
            UserDO userDO = userDAO.getById(id);
            if (! userDO.getPassword().equalsIgnoreCase(oldPassword)) {
                log.warn("some one password new and old not equals");
                throw new RuntimeException(ReturnCodeEnum.USER_OLD_PASSWORD_ERROR.getDesc());
            }

            if (userDO.getPassword().equalsIgnoreCase(newPassword)) {
                throw new RuntimeException(ReturnCodeEnum.SAME_PASSWORD_ERROR.getDesc());
            }
        }
        userDAO.updatePasswordAndClearToken(id, newPassword);

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

        Integer incrementCoins = getIncrementCoins(userId);
        userDAO.incrementCoins(userId, incrementCoins);
        return true;
    }

    // 连续签到可以奖励, 7天内签到，连续每天增加1个硬币，上限2+6=8
    private Integer getIncrementCoins(Long userId) {
        List<CheckInDO> checkInDOList = checkInDAO.listRecentWeek(userId);
        if (CollectionUtils.isEmpty(checkInDOList)) return CoinsDefinitions.CHECK_IN;

        int coins = CoinsDefinitions.CHECK_IN;
        for (int i = 1; i < 7; i++) {
            if (! hasCheckIn(checkInDOList, i)) {
                break;
            }
            coins += 1;
        }
        return coins;
    }

    private boolean hasCheckIn(List<CheckInDO> checkInDOList, int beforeDays) {
        return checkInDOList.stream().anyMatch(e -> DateUtils.formatDay(new Date()).getTime() - e.getDate().getTime() == 24 * 60 * 60 * 1000 * beforeDays);
    }

    public void checkCoins(Long userId, int coins) {
        UserDO userDO = userDAO.getById(userId);
        if (userDO.getCoins() < coins) throw new BusinessException("需要" + coins + "个硬币才能此操作");
    }

    public void consumeCoins(Long userId, int coins) {
        userDAO.incrementCoins(userId, - coins);
    }

    private static final String LIKE_LOCK_PREFIX = "like_lock_";

    @Autowired
    private DistributedLock distributedLock;

    public Boolean like(Long id, Integer type, Boolean like, Long userId) {
        // 这里控制幂等, 现在单实例防并发，后面集群模式需要切换为分布式锁
        String key = LIKE_LOCK_PREFIX + "|" + id + "|" + userId;

        if (! distributedLock.lock(key, 1, TimeUnit.MINUTES)) {
            throw new BusinessException("请稍后操作");
        }
        try {
            // 幂等
            if (userLikeDAO.getLikeList(Lists.newArrayList(id), type, userId).size() > 0) {
                throw new BusinessException("重复点赞");
            }

            UserLikeDO userLikeDO = new UserLikeDO();
            userLikeDO.setTargetId(id);
            userLikeDO.setType(type);
            userLikeDO.setUserId(userId);
            userLikeDO.setValue(like);

            userLikeDAO.insert(userLikeDO);

            // 给目标用户添加硬币
            if (LikeTypeEnum.REPLY.getType().equals(type)) {
                // 添加一个1个币
                ReplyDO replyDO = replyDAO.getById(id);
                if (replyDO == null) throw new BusinessException("回复不存在");

                Long sendUserId = replyDO.getSendUserId();
                if (userId.equals(sendUserId)) throw new BusinessException("禁止给自己点赞");

                userDAO.incrementCoins(sendUserId, 1);
            } else if (LikeTypeEnum.POST.getType().equals(type)) {
                // 帖子被赞增加2个币
                PostDO postDO = postDAO.getById(id);
                if (postDO == null) throw new BusinessException("帖子不存在");

                Long creatorId = postDO.getCreatorId();
                if (userId.equals(creatorId)) throw new BusinessException("禁止给自己点赞");

                userDAO.incrementCoins(creatorId, 2);
            }
        } finally {
            distributedLock.unlock(key);
        }
        return true;
    }

    @Transactional
    @Async("statisticThreadPool")
    public void updateIp(Long userId, String ip) {
        String lockKey = userId.toString() + "_" + ip;
        Boolean lock = distributedLock.lock(lockKey, 10, TimeUnit.SECONDS);
        try {
            if (! lock) return ;
            UserDO userDO = userDAO.getById(userId);
            // 当前与请求的ip一致，不更新
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(userDO.getIp()) && userDO.getIp().equals(ip)) return ;

            log.warn("{} change ip from {} to {}", JSONObject.toJSON(userDO), userDO.getIp(), ip);
            OperateLogDO operateLogDO = new OperateLogDO();
            operateLogDO.setType(OperateTypeEnum.CHANGE_IP.getCode());
            operateLogDO.setUserId(userDO.getId());
            operateLogDAO.insert(operateLogDO);
            userDAO.updateIp(userDO.getId(), ip);

            monitorAbility.sendMsg(userDO.getName() + " 切换ip " + userDO.getIp() + " -> " + ip);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            distributedLock.unlock(lockKey);
        }
    }
}
