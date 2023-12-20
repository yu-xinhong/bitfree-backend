package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.aspect.ParameterCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.GetCoinsRecordReq;
import com.jihai.bitfree.dto.resp.CoinsRecordTypeResp;
import com.jihai.bitfree.dto.resp.OperationResp;
import com.jihai.bitfree.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operation")
public class OperationController extends BaseController {
    @Autowired
    private OperationLogService operationLogService;

    @PostMapping("/getCoinsRecord")
    @LoggedCheck
    @ParameterCheck
    public Result<PageResult<OperationResp>> getCoinsRecord(@RequestBody GetCoinsRecordReq req) {
        return convertSuccessResult(operationLogService.getCoinsRecord(getCurrentUser().getId(), req));
    }

    @GetMapping("/getCoinsTypeList")
    @LoggedCheck
    public Result<List<CoinsRecordTypeResp>> getCoinsTypeList() {
        return convertSuccessResult(operationLogService.getCoinsTypeList());
    }
}
