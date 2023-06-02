package com.jihai.bitfree.controller;


import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.*;
import com.jihai.bitfree.dto.resp.UserDTO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.service.NotifyService;
import com.jihai.bitfree.service.UserService;
import com.jihai.bitfree.utils.DO2DTOConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private NotifyService notifyService;

    @PostMapping("/login")
    @ParameterCheck
    public Result<String> login(@RequestBody LoginReq loginReq) {
        UserDO userDO = userService.queryByEmailAndPassword(loginReq.getEmail(), loginReq.getPassword());
        if (Objects.isNull(userDO)) {
            return convertFailResult(null, "用户或密码错误");
        }
        return convertSuccessResult(userService.generateToken(loginReq.getEmail(), loginReq.getPassword()));
    }

    @PostMapping("/update")
    @LoggedCheck
    @ParameterCheck
    public Result<Boolean> update(@RequestBody UpdateUserReq updateUserReq) {
        return null;
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
        UserDTO currentUser = getCurrentUser();
        userService.logout(currentUser.getId());
        return convertSuccessResult(true);
    }

    @GetMapping("/getDetail")
    @LoggedCheck
    public Result<UserDTO> getDetail(UserDetailReq userDetailReq) {
        if (userDetailReq.getId() == null) return convertSuccessResult(getCurrentUser());
        return convertSuccessResult(DO2DTOConvert.convertUser(userService.getUser(userDetailReq.getId())));
    }


    @PostMapping("/addUser")
    @ParameterCheck
    public Result<String> addUser(@RequestBody AddUserReq addUserReq) {
        String password = userService.addUser(addUserReq.getEmail(), addUserReq.getSecret());
        notifyService.sendNotice(addUserReq.getEmail(), password);
        return convertSuccessResult(password);
    }
}
