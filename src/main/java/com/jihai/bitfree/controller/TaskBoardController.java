package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.TaskBoardReq;
import com.jihai.bitfree.dto.resp.TaskBoardResp;
import com.jihai.bitfree.service.TaskBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/taskBoard")
public class TaskBoardController extends BaseController {

    @Autowired
    private TaskBoardService taskBoardService;

    @GetMapping("/getTaskBoardList")
    @ParameterCheck
    @LoggedCheck
    public Result<PageResult<TaskBoardResp>> getTaskBoardList(TaskBoardReq taskBoardReq) {
        return convertSuccessResult(taskBoardService.pageQueryTaskBoardList(taskBoardReq.getStatus(), taskBoardReq.getPage(), taskBoardReq.getSize()));
    }

}
