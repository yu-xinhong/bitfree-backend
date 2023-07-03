package com.jihai.bitfree.controller;


import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.*;
import com.jihai.bitfree.dto.resp.ActivityUserResp;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.service.NotifyService;
import com.jihai.bitfree.service.UserService;
import com.jihai.bitfree.utils.DO2DTOConvert;
import com.jihai.bitfree.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private PasswordUtils passwordUtils;

    @PostMapping("/login")
    @ParameterCheck
    public Result<String> login(@RequestBody LoginReq loginReq) {
        UserDO userDO = userService.queryByEmailAndPassword(loginReq.getEmail(), loginReq.getPassword().toUpperCase());
        if (Objects.isNull(userDO)) {
            return convertFailResult(null, "邮箱或密码错误");
        }
        return convertSuccessResult(userService.generateToken(loginReq.getEmail(), loginReq.getPassword()));
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
        notifyService.sendNotice(addUserReq.getEmail(), password);
        return convertSuccessResult(password);
    }


    @RequestMapping("/addUser/{secret}/{level}/{email}")
    @ParameterCheck
    public Result<String> addUser(@PathVariable("secret") String secret,
                                  @PathVariable("level") Integer level,
                                  @PathVariable("email") String email) {
        String password = userService.addUser(email, level, secret);
        notifyService.sendNotice(email, password);
        return convertSuccessResult(password);
    }


    @PostMapping("/save")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> save(@RequestBody SaveUserReq saveUserReq) {
        return convertSuccessResult(userService.save(saveUserReq.getAvatar(), saveUserReq.getName(), saveUserReq.getCity(),
                saveUserReq.getPosition(), saveUserReq.getSeniority(),
                getCurrentUser().getId()));
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
    public Result<Boolean> updatePassword(@RequestBody UpdatePasswordReq updatePasswordReq) {
        return convertSuccessResult(userService.updatePassword(getCurrentUser().getId(), updatePasswordReq.getOldPwd(), updatePasswordReq.getPwd()));
    }


}
