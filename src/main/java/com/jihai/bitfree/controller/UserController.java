package com.jihai.bitfree.controller;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jihai.bitfree.ability.MonitorAbility;
import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.constants.LockKeyConstants;
import com.jihai.bitfree.dto.req.*;
import com.jihai.bitfree.dto.resp.ActivityUserResp;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.lock.DistributedLock;
import com.jihai.bitfree.service.NotifyService;
import com.jihai.bitfree.service.UserService;
import com.jihai.bitfree.utils.DO2DTOConvert;
import com.jihai.bitfree.utils.PasswordUtils;
import com.jihai.bitfree.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private MonitorAbility monitorAbility;

    @Autowired
    private DistributedLock distributedLock;


    /**
     * 唯一不需要登录可以直接调用的接口
     * 2023/11/5遭遇撞库攻击
     * 已封禁IP:123.xxx.30.95
     * 添加重试次数拦截，1分钟内超过3次错误，直接封禁5分钟
     * @param loginReq
     * @return
     */
    @PostMapping("/login")
    @ParameterCheck
    public Result<String> login(@RequestBody LoginReq loginReq) {
        loginRequestCheck();
        UserDO userDO = userService.queryByEmailAndPassword(loginReq.getEmail(), loginReq.getPassword().toUpperCase());

        if (Objects.isNull(userDO)) {
            String lockKey = LockKeyConstants.IP_REQUEST + requestUtils.getCurrentIp();
            try {
                AtomicInteger count = requestLoginCache.get(lockKey, () -> new AtomicInteger(0));
                count.incrementAndGet();
                requestLoginCache.put(lockKey, count);

                String returnMsg = "邮箱或密码错误, 剩余次数 " + (3 - count.get()) + " 次";
                monitorAbility.sendMsg(requestUtils.getCurrentIp() + " " + returnMsg);
                return convertFailResult(null, returnMsg);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return convertSuccessResult(userService.generateToken(loginReq.getEmail(), loginReq.getPassword(), requestUtils.getCurrentIp()));
    }

    private Cache<String, AtomicInteger> requestLoginCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(1000).build();

    /**
     * 撞库拦截
     */
    private void loginRequestCheck() {
        String lockKey = LockKeyConstants.IP_REQUEST + requestUtils.getCurrentIp();
        Boolean locked = distributedLock.lock(lockKey, 10, TimeUnit.SECONDS);
        if (! locked) return ;
        try {
            if (requestLoginCache.get(lockKey, () -> new AtomicInteger(0)).intValue() >= 3) {
                monitorAbility.sendMsg(requestUtils.getCurrentIp() + " 账号或密码错误, 已触发限流");
                throw new BusinessException("请稍后再重试");
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            distributedLock.unlock(lockKey);
        }
    }


    @GetMapping("/checkName")
    @LoggedCheck
    @ParameterCheck
    public Result<Boolean> checkName(CheckNameReq checkNameReq) {
        return convertSuccessResult(true);
    }


    @PostMapping("/logout")
    @LoggedCheck
    public Result<Boolean> logout() {
        UserResp currentUser = getCurrentUser();
        userService.logout(currentUser.getId());
        return convertSuccessResult(true);
    }

    @GetMapping("/getDetail")
    @LoggedCheck
    public Result<UserResp> getDetail(UserDetailReq userDetailReq) {
        if (userDetailReq.getId() == null) return convertSuccessResult(getCurrentUser());
        return convertSuccessResult(DO2DTOConvert.convertUser(userService.getUser(userDetailReq.getId())));
    }


    @PostMapping("/addUser")
    @ParameterCheck
    public Result<String> addUser(@RequestBody AddUserReq addUserReq) {
        String password = userService.addUser(addUserReq.getEmail().trim(), addUserReq.getLevel(), addUserReq.getSecret());
        notifyService.sendNotice(addUserReq.getEmail(), password, addUserReq.getLevel());
        return convertSuccessResult(password);
    }


    @RequestMapping("/addUser/{secret}/{level}/{email}")
    @ParameterCheck
    public Result<String> addUser(@PathVariable("secret") String secret,
                                  @PathVariable("level") Integer level,
                                  @PathVariable("email") String email) {
        String password = userService.addUser(email, level, secret);
        notifyService.sendNotice(email, password, level);
        return convertSuccessResult(password);
    }


    @PostMapping("/save")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> save(@RequestBody SaveUserReq saveUserReq) {
        return convertSuccessResult(userService.save(saveUserReq.getAvatar(), saveUserReq.getName(), saveUserReq.getCity(),
                saveUserReq.getPosition(), saveUserReq.getSeniority(),
                getCurrentUser().getId(), getCurrentUser().getName()));
    }


    @GetMapping("/hadModifyPwd")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> hadModifyPwd() {
        return convertSuccessResult(userService.hadModifyPwd(getCurrentUser().getId()));
    }

    @GetMapping("/getActivityList")
    @ParameterCheck
    @LoggedCheck
    public Result<List<ActivityUserResp>> getActivityList() {
        return convertSuccessResult(userService.getActivityList());
    }


    @PostMapping("/resetPassword")
    @ParameterCheck
    public Result<Boolean> resetPassword(@RequestBody ResetPasswordReq resetPasswordReq) {
        return convertSuccessResult(userService.resetPassword(resetPasswordReq.getId() , resetPasswordReq.getSecret(), passwordUtils.defaultPassword()));
    }


    @PostMapping("/updatePassword")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> updatePassword(@RequestBody UpdatePasswordReq updatePasswordReq) {
        return convertSuccessResult(userService.updatePassword(getCurrentUser().getId(), updatePasswordReq.getOldPwd(), updatePasswordReq.getPwd()));
    }


    @GetMapping("/getCheckIn")
    @LoggedCheck
    public Result<Boolean> getCheckIn() {
        return convertSuccessResult(userService.getCheckIn(getCurrentUser().getId()));
    }

    @PostMapping("/checkIn")
    @LoggedCheck
    public Result<Boolean> checkIn() {
        return convertSuccessResult(userService.checkIn(getCurrentUser().getId()));
    }

    @PostMapping("/like")
    @LoggedCheck
    public Result<Boolean> like(@RequestBody LikeReq likeReq) {
        return convertSuccessResult(userService.like(likeReq.getId(), likeReq.getType(), likeReq.getLike(), getCurrentUser().getId()));
    }


}
