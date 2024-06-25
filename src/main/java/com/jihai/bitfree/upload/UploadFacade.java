package com.jihai.bitfree.upload;

import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.upload.lb.RoundRobinSelector;
import com.jihai.bitfree.upload.lb.UploadTypeSelector;
import com.jihai.bitfree.upload.type.UploadTypeRegistry;
import java.io.File;
import java.util.List;
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
        List<String> uploadTypeList = UploadTypeRegistry.getTypes();
        final int MAX_RETRY_ATTEMPTS = uploadTypeList.size();
        int attempts = 0;
        String linkUrl;

        while (attempts < MAX_RETRY_ATTEMPTS) {
            String uploadType = SELECTOR.select(UploadTypeRegistry.getTypes());
            UploadAbility uploadAbility = UploadTypeRegistry.resolve(uploadType);

            try {
                linkUrl = uploadAbility.doUpload(uploadFile);
                return linkUrl;
            } catch (Exception e) {
                log.error("Upload failed with upload type: {}. Attempt: {}/{}. Error: {}",
                        uploadType, attempts + 1, MAX_RETRY_ATTEMPTS, e.getMessage());
                attempts++;
            }
        }
        throw new BusinessException("All upload attempts failed.");
    }

}

