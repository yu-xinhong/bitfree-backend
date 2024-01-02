package com.jihai.bitfree.controller;

import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.PageQueryReq;
import com.jihai.bitfree.dto.resp.OrderResp;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author McDull
 */
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/userOrderList")
    public Result<PageResult<OrderResp>> userOrderList(PageQueryReq page) {
        UserResp user = getCurrentUser();
        PageResult<OrderResp> pageResult = orderService.pageUserOrder(user.getId(), page.getPage(), page.getSize());
        return convertSuccessResult(pageResult);
    }
}
