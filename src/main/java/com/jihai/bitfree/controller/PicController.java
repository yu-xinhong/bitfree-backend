package com.jihai.bitfree.controller;


import cn.hutool.core.util.StrUtil;
import com.jihai.bitfree.aspect.LoggedCheck;
import com.jihai.bitfree.base.BaseController;
import com.jihai.bitfree.base.Result;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.upload.UploadFacade;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pic")
@RequiredArgsConstructor
public class PicController extends BaseController {

    private final UploadFacade uploadFacade;

    /**
     * TODO 考虑限流
     */
    @PostMapping("upload")
    @LoggedCheck
    public Result<String> uploadPic(@RequestParam("file") MultipartFile multipartFile)
            throws IOException {
        // 限制单张图片大小：最大 2MB
        if (multipartFile.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过 2MB");
        }
        return convertSuccessResult(uploadFacade.upload(multipartFileToFile(multipartFile)));
    }

    public static File multipartFileToFile(MultipartFile multiFile) throws IOException {
        String fileName = multiFile.getOriginalFilename();
        if (StrUtil.isBlank(fileName)) {
            throw new BusinessException("文件名不能为空");
        }
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // 若需要防止生成的临时文件重复,可以在文件名后添加随机码
        File file = File.createTempFile(fileName, prefix);
        multiFile.transferTo(file);
        return file;
    }
}
