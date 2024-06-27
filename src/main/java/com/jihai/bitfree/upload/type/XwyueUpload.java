package com.jihai.bitfree.upload.type;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.upload.UploadTemplate;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <a href="https://img.xwyue.com/dashboard">Xwyue</a>
 */
@Slf4j
@Component
public class XwyueUpload extends UploadTemplate {

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String STATUS = "status";
    private static final String TOKEN = "token";
    private static final String USER_TOKEN_PATH = "/tokens";

    @Override
    public String getUrl() {
        return configService.getByKey(Constants.XWYUE_URL);
    }

    @Override
    public String getUserToken() {
        String userToken = configService.getByKey(Constants.XWYUE_USER_TOKEN);
        String[] tokens = userToken.split(" ");
        String email = tokens[0];
        String password = tokens[1];

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(EMAIL, email);
        bodyMap.put(PASSWORD, password);

        JSONObject obj;
        try (HttpResponse httpResponse = executePostRequest(getUrl() + USER_TOKEN_PATH, bodyMap,
                new HashMap<>())) {
            obj = JSONUtil.parseObj(httpResponse.body());
        }
        if (obj.getBool(STATUS)) {
            return obj.getJSONObject(DATA).getStr(TOKEN);
        }
        throw new BusinessException(Constants.GET_TOKEN_ERROR_LOG);
    }

    @Override
    public String getUploadToken(String userToken) {
        return userToken;
    }

    @Override
    public HttpResponse upload(File uploadFile, String uploadToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, BEARER + uploadToken);

        Map<String, Object> body = new HashMap<>();
        body.put(FILE, uploadFile);

        return executeUploadRequest(getUrl() + UPLOAD_FILE_PATH, headers, body);
    }

    @Override
    public String parseResponse(HttpResponse response) {
        JSONObject obj = JSONUtil.parseObj(response.body());
        if (!obj.getBool(STATUS)) {
            throw new BusinessException(obj.getStr(MESSAGE));
        }
        JSONObject data = obj.getJSONObject(DATA);
        return data.getJSONObject(LINKS).getStr(URL);
    }

    @Override
    public String getType() {
        return "xwyue";
    }
}
