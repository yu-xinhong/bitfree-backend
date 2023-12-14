package com.jihai.bitfree.service;


import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jihai.bitfree.ability.MonitorAbility;
import com.jihai.bitfree.base.enums.LikeTypeEnum;
import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.base.enums.UserLevelEnum;
import com.jihai.bitfree.bo.UserRemarkBO;
import com.jihai.bitfree.constants.CoinsDefinitions;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.constants.LockKeyConstants;
import com.jihai.bitfree.dao.*;
import com.jihai.bitfree.dto.resp.ActivityUserResp;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.*;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.lock.DistributedLock;
import com.jihai.bitfree.support.Observable;
import com.jihai.bitfree.support.ReadNotificationEvent;
import com.jihai.bitfree.support.TransactionUtils;
import com.jihai.bitfree.utils.DO2DTOConvert;
import com.jihai.bitfree.utils.DateUtils;
import com.jihai.bitfree.utils.PasswordUtils;
import com.jihai.bitfree.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.jihai.bitfree.constants.CoinsDefinitions.INVITED_USER_COINS;
import static com.jihai.bitfree.constants.CoinsDefinitions.INVITE_USER_COINS;
import static com.jihai.bitfree.constants.LockKeyConstants.UPDATE_USER;

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

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private Observable observable;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private PasswordUtils passwordUtils;
    @Autowired
    private OperationLogService operationLogService;

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

    @Transactional
    public String addUser(String email, Integer level, String secret) {

        checkSecret(secret);
        // 校验
        if (userDAO.queryByEmail(email) != null) {
            log.error("email {} is duplicated", email);
            throw new RuntimeException("邮箱重复创建");
        }

        UserDO userDO = new UserDO();
        // 随机设置新用户头像
        String[] avatarList = configDAO.getByKey(DEFAULT_AVATAR).getValue().split(",");
        Random random = new Random();
        userDO.setAvatar(avatarList[random.nextInt(avatarList.length)]);
        userDO.setEmail(email);

        String password = PasswordUtils.generatePwd();
        userDO.setPassword(PasswordUtils.md5(password));

        userDO.setLevel(level);
        userDO.setName("bit" + RandomUtils.nextInt(0, 9999));

        userDAO.insert(userDO);

        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setUserId(userDO.getId());
        operateLogDO.setType(OperateTypeEnum.INIT_USER.getCode());

        operateLogDAO.insert(operateLogDO);

        // 给用户发送私信通知
        sendNotification(userDO.getId());
        return password;
    }

    private void sendNotification(Long userId) {
        Boolean locked = distributedLock.lock(LockKeyConstants.SEND_NOTIFICATION, 1, TimeUnit.MINUTES);
        if (!locked) return;

        try {
            ConfigDO configKey = configDAO.getByKey(Constants.MODIFY_SETTINGS_NOTIFICATION_ID);
            NotificationDO notificationDO = notificationDAO.getById(Long.valueOf(configKey.getValue()));

            notificationDAO.updateUserIdListById(notificationDO.getId(), notificationDO.getUserList() + "," + userId);
        } finally {
            distributedLock.unlock(LockKeyConstants.SEND_NOTIFICATION);
        }

    }


    private void checkSecret(String secret) {
        ConfigDO configDO = configDAO.getByKey(Constants.SECRET);
        if (!configDO.getValue().equals(secret)) {
            log.error("warn ! this operation only be allowed to administrator jihai!");
            // send alert
            throw new RuntimeException(ReturnCodeEnum.SECRET_ERROR.getDesc());
        }
    }

    public UserResp getByToken(String token) {
        return DO2DTOConvert.convertUser(userDAO.getByToken(token));
    }

    public Boolean save(String avatar, String name, String city, String position, Integer seniority, String github, Long inviteUserId, Long userId, String currentName, Integer level) {
        if (!StringUtils.isEmpty(avatar) && avatar.contains("jihai")) throw new BusinessException("禁止使用该头像");
        if (!StringUtils.isEmpty(name) && name.contains("极海")) throw new BusinessException("禁止使用该昵称");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(github) && ! UserLevelEnum.ULTIMATE.getLevel().equals(level)) {
            throw new BusinessException("请升级旗舰版才可关联Github~");
        }
        if (userId.equals(inviteUserId)) throw new BusinessException("禁止邀请自己");

        Boolean locked = distributedLock.lock(UPDATE_USER + userId, 1, TimeUnit.SECONDS);
        if (! locked) throw new BusinessException("请稍后再操作");

        try {
            if (!name.equals(currentName)) {
                // 修改名字，不会再提示通知，这里是DB操作，可以保证后面出异常回滚这里
                observable.notify(new ReadNotificationEvent(userId, Long.valueOf(configDAO.getByKey(Constants.MODIFY_SETTINGS_NOTIFICATION_ID).getValue())));
            }
            transactionTemplate.execute(action -> {
                boolean hasInvited = true;
                if (inviteUserId != null && checkInvited(userId)) {
                    hasInvited = false;
                    UserDO inviteUser = userDAO.getById(inviteUserId);
                    if (inviteUser == null) throw new BusinessException("邀请人不存在");

                    // 填写邀请人后，双方添加硬币
                    incrementCoins(inviteUserId, INVITE_USER_COINS, OperateTypeEnum.INVITE_COINS);
                    incrementCoins(userId, INVITED_USER_COINS, OperateTypeEnum.INVITED_COINS);
                }
                //  当该名字有人使用且当前名字不等于待修改名字时, 返回提示
                if (!name.equals(currentName) && userDAO.countByName(name) > 0) throw new BusinessException("该名称已被使用");
                userDAO.save(userId, avatar, name, city, position, seniority, github, hasInvited ? null : inviteUserId);
                return null;
            });
        } finally {
            distributedLock.unlock(UPDATE_USER + userId);
        }
        return true;
    }

    private boolean checkInvited(Long userId) {
        return operateLogDAO.queryByUserIdAndType(userId, OperateTypeEnum.INVITED_COINS.getCode()).equals(0);
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
            if (!userDO.getPassword().equalsIgnoreCase(oldPassword)) {
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
            if (!hasCheckIn(checkInDOList, i)) {
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
        userDAO.incrementCoins(userId, -coins);
    }

    public Boolean like(Long id, Integer type, Boolean like, Long userId) {
        // 这里控制幂等, 现在单实例防并发，后面集群模式需要切换为分布式锁
        String key = LockKeyConstants.LIKE + "|" + id + "|" + userId;

        if (!distributedLock.lock(key, 1, TimeUnit.MINUTES)) {
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

    /**
     * 第一个问题：
     * 这里如果有事务，会存在并发问题
     * T1 获取分布式锁，正常处理，发送通知
     * T1 释放分布式锁，未提交事务.
     * T2 获取到分布式锁
     * T2 userDAO.getById(userId)查出来仍旧是老ip的，开始通知并更新user
     * T1 提交事务
     * <p>
     * 引入编程式事务，优化为先加锁再执行事务，并且保证事务尽量小
     * <p>
     * 第二个问题：
     * if (! lock) return ; 这一行代码应该移动到try 之前。
     * 因为没获取到锁仍会执行finally 释放另一个线程的分布式锁，这会导致分布式锁形同虚设。
     *
     * @param userId
     * @param ip
     */

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Async("commonAsyncThreadPool")
    public void updateIp(Long userId, String ip) {
        String lockKey = LockKeyConstants.UPDATE_IP + userId.toString() + "_" + ip;
        Boolean lock = distributedLock.lock(lockKey, 10, TimeUnit.SECONDS);
        if (!lock) return;
        try {
            UserDO userDO = userDAO.getById(userId);
            // 当前与请求的ip一致，不更新
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(userDO.getIp()) && userDO.getIp().equals(ip)) return;
            log.warn("{} change ip from {} to {}", JSONObject.toJSON(userDO), userDO.getIp(), ip);
            OperateLogDO operateLogDO = new OperateLogDO();
            operateLogDO.setType(OperateTypeEnum.CHANGE_IP.getCode());
            operateLogDO.setUserId(userDO.getId());

            transactionTemplate.execute((status) -> {
                operateLogDAO.insert(operateLogDO);
                userDAO.updateIp(userDO.getId(), ip);
                return null;
            });
            monitorAbility.sendMsg(userDO.getName() + " 切换ip " + userDO.getIp() + " -> " + ip);
        } finally {
            distributedLock.unlock(lockKey);
        }

    }

    @Autowired
    private TransactionUtils transactionUtils;

    public Boolean resetPassword(Long id, String email) {
        String password = PasswordUtils.generatePwd();

        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setType(OperateTypeEnum.RESET_PASSWORD.getCode());
        operateLogDO.setUserId(id);

        return transactionTemplate.execute((action) -> {
            operateLogDAO.insert(operateLogDO);
            userDAO.updatePasswordAndClearToken(id, PasswordUtils.md5(password));
            // 事务执行成功再处理
            transactionUtils.doAfterTransaction(() -> {
                // 这里level只是指定发送邮件的模板内容，重置密码用level为社区的模板即可
                notifyService.sendNotice(email, password, UserLevelEnum.COMMUNITY.getLevel());
                log.info("reset password email {} ", email);
            });
            return true;
        });
    }

    public UserDO getByEmail(String email) {
        return userDAO.getByEmail(email);
    }


    public List<UserDO> getRanksByCoins() {
        List<UserDO> userRankByCoins = userDAO.getRanksByCoins();
        return userRankByCoins;
    }

    public int getUserRank(Long userId) {
        return userDAO.getUserRank(userId);
    }

    public List<UserResp> searchUser(String name) {
        name = name.replace("@", "");
        List<UserDO> userDOList = userDAO.searchUser("%" + name + "%");
        return DO2DTOConvert.convertUsers(userDOList);
    }


    public Boolean updateVoiceState(Long userId, Integer voiceState) {
        UserDO userDO = userDAO.getById(userId);
        UserRemarkBO userRemarkBO = JSON.parseObject(userDO.getRemark(), UserRemarkBO.class);
        userRemarkBO.setVoiceState(voiceState);
        userDAO.updateRemark(userId, JSON.toJSONString(userRemarkBO));
        return true;
    }

    public Integer getVoiceState(Long userId) {
        UserDO userDO = userDAO.getById(userId);
        UserRemarkBO userRemarkBO = JSON.parseObject(userDO.getRemark(), UserRemarkBO.class);
        return ObjUtil.isNull(userRemarkBO) || ObjUtil.isNull(userRemarkBO.getVoiceState()) ? 1 : userRemarkBO.getVoiceState();
    }
    /**
     * 更新硬币数量
     *
     * @param userId 用户id
     * @param coins 修改的硬币数
     * @param operateType 操作类型
     * @return 是否更新成功
     */
    public boolean incrementCoins(Long userId, int coins, OperateTypeEnum operateType) {
        int result = userDAO.incrementCoins(userId, coins);
        if (operateType != null) {
            operationLogService.asynSaveOperateLog(userId, operateType);
        }
        return result >= 1;
    }
}
