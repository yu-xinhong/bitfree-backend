package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController extends BaseController {

    @Autowired
    private ConfigService configService;

    @GetMapping("/getDefaultPoster")
    @LoggedCheck
    @ParameterCheck
    public Result<String> getDefaultPoster() {
        return convertSuccessResult(configService.getByKey(Constants.DEFAULT_POSTER));
    }
}
