package com.jihai.bitfree.controller;

import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.dto.req.GetFileReq;
import com.jihai.bitfree.dto.resp.FileUploadResp;
import com.jihai.bitfree.dto.resp.GetFileResp;
import com.jihai.bitfree.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    @Autowired
    private FileService fileService;


    @PostMapping("/upload")
    @LoggedCheck
    public Result<FileUploadResp> upload(@RequestParam("file") MultipartFile multipartFile) {
        return convertSuccessResult(fileService.upload(multipartFile, getCurrentUser().getId()));
    }

    @GetMapping("/getById")
    @LoggedCheck
    public Result<GetFileResp> getUrlById(GetFileReq getFileReq) {
        GetFileResp getFileResp = new GetFileResp();
        getFileResp.setId(getFileResp.getId());
        getFileResp.setUrl(fileService.getUrlById(getFileReq.getId()));
        return convertSuccessResult(getFileResp);
    }
}
