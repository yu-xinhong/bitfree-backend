package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.AddNodeReq;
import com.jihai.bitfree.dto.req.VerifyNodeReq;
import com.jihai.bitfree.dto.resp.QuestionNodeResp;
import com.jihai.bitfree.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
public class QuestionController extends BaseController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("getTree")
    @LoggedCheck
    public Result<List<QuestionNodeResp>> getTree() {
        return convertSuccessResult(questionService.getTree());
    }

    @PostMapping("addNode")
    @LoggedCheck
    public Result<Boolean> addNode(@RequestBody AddNodeReq addNodeReq) {
        return convertSuccessResult(questionService.addNode(addNodeReq.getParentId(), addNodeReq.getContent(), getCurrentUser().getId()));
    }

    @PostMapping("verify")
    @LoggedCheck
    public Result<Boolean> verify(@RequestBody VerifyNodeReq verifyNodeReq) {
        return convertSuccessResult(questionService.verify(verifyNodeReq.getNodeId(), verifyNodeReq.getStatus(), getCurrentUser().getId()));
    }

    @GetMapping("verifyRight")
    @LoggedCheck
    public Result<Boolean> verifyRight() {
        return convertSuccessResult(questionService.verifyRight(getCurrentUser().getId()));
    }

    @GetMapping("detail")
    @LoggedCheck
    public Result<QuestionNodeResp> getDetail(@RequestParam(value = "id") Long questionId) {
        return convertSuccessResult(questionService.getDetail(questionId));
    }

}
