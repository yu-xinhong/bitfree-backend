package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.CommTaskReq;
import com.jihai.bitfree.dto.req.TaskBoardReq;
import com.jihai.bitfree.dto.resp.TaskBoardResp;
import com.jihai.bitfree.service.TaskBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    
    @PostMapping("/applyForTask")
    @LoggedCheck
    public Result<Boolean> applyForTask(@RequestBody @Valid CommTaskReq commTaskReq){
        return convertSuccessResult(taskBoardService.applyForTask(getCurrentUser().getId(), commTaskReq.getTaskId()));
    }

    @PostMapping("/completeTask")
    @LoggedCheck
    public Result<Boolean> completeTask(@RequestBody @Valid CommTaskReq commTaskReq){
        return convertSuccessResult(taskBoardService.completeTask(getCurrentUser().getId(), commTaskReq.getTaskId()));
    }

    @PostMapping("/cancelTask")
    @ParameterCheck
    @LoggedCheck
    public Result<Boolean> cancelTask(@RequestBody CommTaskReq commTaskReq){
        return convertSuccessResult(taskBoardService.cancelTask(getCurrentUser().getId(), commTaskReq.getTaskId()));
    }

}
