package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.IpLimiterAspect;
import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dto.req.ModifyLimitReq;
import com.jihai.bitfree.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config")
public class ConfigController extends BaseController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private IpLimiterAspect ipLimiterAspect;

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
}
