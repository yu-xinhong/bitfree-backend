package com.jihai.bitfree.upload;

import cn.hutool.http.HttpResponse;
import com.jihai.bitfree.service.ConfigService;
import com.jihai.bitfree.upload.type.UploadTypeRegistry;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UploadTemplate implements UploadAbility, UploadTypeRegistrable {

    @Autowired
    protected ConfigService configService;

    public String doUpload(File uploadFile) {
        String userToken = getUserToken();
        String uploadToken = getUploadToken(userToken);
        HttpResponse response = upload(uploadFile, uploadToken);
        return parseResponse(response);
    }

    @Override
    public void registry() {
        UploadTypeRegistry.register(getType(), this);
    }
}
