package com.jihai.bitfree.upload;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.jihai.bitfree.service.ConfigService;
import com.jihai.bitfree.upload.type.UploadTypeRegistry;
import java.io.File;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class UploadTemplate implements UploadAbility, UploadTypeRegistrable {

    protected static final String BEARER = "Bearer ";
    protected static final String AUTHORIZATION = "Authorization";
    protected static final String DATA = "data";
    protected static final String FILE = "file";
    protected static final String URL = "url";
    protected static final String MESSAGE = "message";
    protected static final String LINKS = "links";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final String MULTIPART_FORM_DATA = "multipart/form-data";
    protected static final String UPLOAD_FILE_PATH = "/upload";


    @Autowired
    protected ConfigService configService;

    @Override
    public void registry() {
        UploadTypeRegistry.register(getType(), this);
    }

    @Override
    public String doUpload(File uploadFile) {
        String userToken = getUserToken();
        String uploadToken = getUploadToken(userToken);
        HttpResponse response = upload(uploadFile, uploadToken);
        return parseResponse(response);
    }

    // region hooks
    protected abstract String getUrl();

    protected abstract String getUserToken();

    protected abstract String getUploadToken(String userToken);

    protected abstract HttpResponse upload(File uploadFile, String uploadToken);

    protected abstract String parseResponse(HttpResponse response);
    // endregion

    protected final HttpResponse executePostRequest(String url, Map<String, Object> body,
            Map<String, String> headers) {
        log.info("request url: {}, body: {}, headers: {}", url, body, headers);
        HttpRequest request = HttpRequest.post(url).header(Header.CONTENT_TYPE, APPLICATION_JSON);
        headers.forEach(request::header);
        if (body != null) {
            request.body(JSONUtil.toJsonStr(body));
        }
        return request.execute();
    }

    protected final HttpResponse executeUploadRequest(String url, Map<String, String> headers,
            Map<String, Object> body) {
        log.info("request url: {}, body: {}, headers: {}", url, body, headers);
        HttpRequest request = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, MULTIPART_FORM_DATA)
                .form(body);
        headers.forEach(request::header);
        return request.execute();
    }
}
