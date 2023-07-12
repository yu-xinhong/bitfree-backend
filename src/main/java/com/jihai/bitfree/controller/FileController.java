package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.FileUploadReq;
import com.jihai.bitfree.dto.req.GetFileReq;
import com.jihai.bitfree.dto.resp.FileUploadResp;
import com.jihai.bitfree.dto.resp.GetFileResp;
import com.jihai.bitfree.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    @Autowired
    private FileService fileService;


    @PostMapping("/upload")
    @LoggedCheck
    public Result<FileUploadResp> upload(@RequestBody FileUploadReq fileUploadReq) {
        return convertSuccessResult(fileService.uploadByMannal(fileUploadReq.getFileUrl(), fileUploadReq.getPoster(), getCurrentUser().getId()));
    }

    @GetMapping("/getById")
    @LoggedCheck
    public Result<GetFileResp> getById(GetFileReq getFileReq) {
        return convertSuccessResult(fileService.getById(getFileReq.getId()));
    }
}
