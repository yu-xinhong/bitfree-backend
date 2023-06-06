package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.resp.TopicResp;
import com.jihai.bitfree.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/topic")
public class TopicController extends BaseController {


    @Autowired
    private TopicService topicService;

    @ParameterCheck
    @LoggedCheck
    @GetMapping("getAllTopic")
    public Result<List<TopicResp>> getAllTopic() {
        return convertSuccessResult(topicService.getAllTopic());
    }
}
