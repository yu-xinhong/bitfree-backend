package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.MessageReq;
import com.jihai.bitfree.dto.req.SendMessageReq;
import com.jihai.bitfree.dto.resp.MessageResp;
import com.jihai.bitfree.dto.resp.UserResp;
import com.jihai.bitfree.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/getRecentList")
    @LoggedCheck
    public Result<PageResult<MessageResp>> getRecentList(MessageReq messageReq) {
        return convertSuccessResult(messageService.pageQueryMessageList(messageReq.getPage(), messageReq.getSize(), getCurrentUser()));
    }

    @PostMapping("/sendMessage")
    @LoggedCheck
    public Result<Boolean> sendMessage(@RequestBody SendMessageReq sendMessageReq) {
        return convertSuccessResult(messageService.sendMessage(sendMessageReq.getContent(), getCurrentUser().getId()));
    }

    @GetMapping("/getLiveUserList")
    @LoggedCheck
    public Result<List<UserResp>> getLiveUserList() {
        return convertSuccessResult(messageService.getLiveUserCache());
    }

    @GetMapping("/getRecentMessageCount")
    @LoggedCheck
    public Result<Integer> getRecentMessageCount() {
        return convertSuccessResult(messageService.getRecentMessageCount());
    }
}
