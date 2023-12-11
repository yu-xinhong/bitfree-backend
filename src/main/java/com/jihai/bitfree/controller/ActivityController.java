package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.ActivityKillReq;
import com.jihai.bitfree.dto.req.ActivityRightReq;
import com.jihai.bitfree.dto.req.GetOrderReq;
import com.jihai.bitfree.dto.req.OrderDetailReq;
import com.jihai.bitfree.dto.resp.ActivityResp;
import com.jihai.bitfree.dto.resp.OrderResp;
import com.jihai.bitfree.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("activity")
public class ActivityController extends BaseController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("getRecent")
    @LoggedCheck
    public Result<ActivityResp> getRecent() {
        return convertSuccessResult(activityService.getRecent());
    }


    @PostMapping("kill")
    @LoggedCheck
    public Result<Boolean> kill(@RequestBody ActivityKillReq activityKillReq) {
        return convertSuccessResult(activityService.kill(getCurrentUser().getId(), activityKillReq.getActivityId()));
    }

    @PostMapping("submitOrderDetail")
    @LoggedCheck
    public Result<Boolean> submitInfo(@RequestBody OrderDetailReq orderDetailReq) {
        return convertSuccessResult(activityService.submitInfo(getCurrentUser().getId(), orderDetailReq.getActivityId(), orderDetailReq.getName(),
                orderDetailReq.getAddress(), orderDetailReq.getSize(), orderDetailReq.getColor(), orderDetailReq.getTel()));
    }

    @GetMapping("getRight")
    @LoggedCheck
    public Result<Boolean> getRight(ActivityRightReq activityRightReq) {
        return convertSuccessResult(activityService.getRight(getCurrentUser().getId(), activityRightReq.getActivityId()));
    }


    @GetMapping("getOrder")
    @LoggedCheck
    public Result<OrderResp> getOrder(GetOrderReq getOrderReq) {
        return convertSuccessResult(activityService.getOrder(getOrderReq.getActivityId(), getCurrentUser().getId()));
    }
}
