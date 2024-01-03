package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.IpLimiterAspect;
import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dto.req.ModifyLimitReq;
import com.jihai.bitfree.service.ConfigService;
import com.jihai.bitfree.support.ConfigSynchronizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/config")
public class ConfigController extends BaseController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private IpLimiterAspect ipLimiterAspect;

    @Autowired
    private ConfigSynchronizer configSynchronizer;

    @GetMapping("/getDefaultPoster")
    @LoggedCheck
    @ParameterCheck
    public Result<String> getDefaultPoster() {
        return convertSuccessResult(configService.getByKey(Constants.DEFAULT_POSTER));
    }

    @PostMapping("/modifyLimit")
    public Result<Boolean> modifyLimit(@RequestBody ModifyLimitReq modifyLimitReq) {
        checkSecret(modifyLimitReq.getSecret());
        ipLimiterAspect.modifyCount(modifyLimitReq.getCount());
        return convertSuccessResult(true);
    }

    @PostMapping("/syncPresetConfig/{secret}")
    @ParameterCheck
    public Result<Boolean> syncPresetConfig(@PathVariable("secret") String secret){
        checkSecret(secret);
        configSynchronizer.refresh();
        return convertSuccessResult(true);
    }

}
