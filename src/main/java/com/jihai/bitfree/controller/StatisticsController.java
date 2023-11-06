package com.jihai.bitfree.controller;


import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.resp.WebStaticsResp;
import com.jihai.bitfree.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController extends BaseController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/webStatistics")
    @LoggedCheck
    public Result<WebStaticsResp> webStatistics() {
        WebStaticsResp webStaticsResp = statisticService.webStatistics();
        webStaticsResp.setUserLoginCount(webStaticsResp.getUserLoginCount() * 8 + 8);
        return convertSuccessResult(webStaticsResp);
    }
}
