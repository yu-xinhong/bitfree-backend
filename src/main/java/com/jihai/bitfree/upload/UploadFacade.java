package com.jihai.bitfree.upload;

import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.enums.ReturnCodeEnum;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.upload.lb.RoundRobinSelector;
import com.jihai.bitfree.upload.lb.UploadTypeSelector;
import com.jihai.bitfree.upload.type.UploadTypeRegistry;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UploadFacade {

    private static final UploadTypeSelector SELECTOR = new RoundRobinSelector();

    /**
     * 动态选择策略： 1. 轮询
     * <p>
     * 考虑的问题： 1. 可用性考虑：调用失败，更改源重试
     * <p>
     */
    public String upload(File uploadFile) {
        int attempts = 0;

        while (attempts < Constants.IMAGE_UPLOAD_RETRY_TIMES) {
            String uploadType = SELECTOR.select(UploadTypeRegistry.getTypes());
            UploadAbility uploadAbility = UploadTypeRegistry.resolve(uploadType);

            try {
                return uploadAbility.doUpload(uploadFile);
            } catch (Exception e) {
                log.error(Constants.IMAGE_UPLOAD_ERROR_LOG_TEMPLATE, uploadType, attempts + 1,
                        Constants.IMAGE_UPLOAD_RETRY_TIMES, e.getMessage());
                attempts++;
            }
        }
        throw new BusinessException(ReturnCodeEnum.IMAGE_UPLOAD_FAIL);
    }

}

