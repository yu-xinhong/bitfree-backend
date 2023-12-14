package com.jihai.bitfree.controller;


import com.google.common.collect.Maps;
import com.jihai.bitfree.ability.MonitorAbility;
import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/baseService")
public class BaseServiceController {

    @Autowired
    private MonitorAbility monitorAbility;

    @Autowired
    private OperationLogService operationLogService;

    private Map<String, String> redirectMapping = Maps.newHashMap();

    {
        redirectMapping.put("dp", "https://item.jd.com/14187350.html");
    }

    @GetMapping("/{shortLink}")
    public RedirectView shortLink(@PathVariable("shortLink") String shortLink) {
        monitorAbility.sendMsg("短链跳转 " + shortLink);
        operationLogService.asynSaveOperateLog(Constants.SYSTEM_DEFAULT_USER_ID, OperateTypeEnum.SHORT_LINK);
        return new RedirectView(redirectMapping.get(shortLink));
    }
}
