package com.jihai.bitfree.upload;

import cn.hutool.http.HttpResponse;
import java.io.File;

public interface UploadAbility {

    String doUpload(File uploadFile);

    String getUrl();

    String getUserToken();

    String getUploadToken(String userToken);

    HttpResponse upload(File uploadFile, String uploadToken);

    String parseResponse(HttpResponse response);
}
