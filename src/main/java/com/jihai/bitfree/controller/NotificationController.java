package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.NotificationDetailReq;
import com.jihai.bitfree.dto.req.NotificationResp;
import com.jihai.bitfree.dto.req.PageQueryReq;
import com.jihai.bitfree.dto.req.ReadNotificationReq;
import com.jihai.bitfree.dto.resp.NotificationDetailResp;
import com.jihai.bitfree.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("notification")
public class NotificationController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("pageQuery")
    @LoggedCheck
    public Result<PageResult<NotificationResp>> pageQuery(PageQueryReq pageQueryReq) {
        List<NotificationResp> notificationRespList = notificationService.pageQuery((pageQueryReq.getPage() - 1) * pageQueryReq.getSize(), pageQueryReq.getSize(), getCurrentUser().getId());
        Integer total = notificationService.total();
        return convertSuccessResult(new PageResult<>(notificationRespList, total));
    }

    @GetMapping("unReadCount")
    @LoggedCheck
    public Result<Integer> unReadCount() {
        return convertSuccessResult(notificationService.unReadNotificationCount(getCurrentUser().getId()));
    }

    @GetMapping("detail")
    @LoggedCheck
    public Result<NotificationDetailResp> detail(NotificationDetailReq notificationDetailReq) {
        return convertSuccessResult(notificationService.detail(notificationDetailReq.getId()));
    }

    @PostMapping("read")
    @LoggedCheck
    public Result<Boolean> read(@RequestBody ReadNotificationReq readNotificationReq) {
        return convertSuccessResult(notificationService.read(readNotificationReq.getId(), getCurrentUser().getId()));
    }
}
