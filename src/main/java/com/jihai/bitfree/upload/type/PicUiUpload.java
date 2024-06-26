package com.jihai.bitfree.upload.type;

import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
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
 * <a href="https://picui.cn/page/api-docs.html">PIC UI</a>
 */
@Slf4j
@Component
public class PicUiUpload extends UploadTemplate {

    private static final String NUM = "num";
    private static final String SECONDS = "seconds";
    private static final String TOKENS = "tokens";

    private static final String TMP_TOKEN = "tmpToken";
    private static final String PERMISSION = "permission";
    private static final String STATUS = "status";

    private static final String UPLOAD_TOKEN_PATH = "/images/tokens";


    @Override
    public String getUrl() {
        return configService.getByKey(Constants.PICUI_URL);
    }

    @Override
    public String getUserToken() {
        return configService.getByKey(Constants.PICUI_USER_TOKEN);
    }

    @Override
    public String getUploadToken(String userToken) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(NUM, 1);
        bodyMap.put(SECONDS, 60);

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.AUTHORIZATION.getValue(), BEARER + userToken);

        String respJson;
        try (HttpResponse httpResponse = executePostRequest(getUrl() + UPLOAD_TOKEN_PATH,
                bodyMap, headers)) {
            respJson = httpResponse.body();
        }
        JSONArray jsonArray = JSONUtil.parseObj(respJson)
                .getJSONObject(DATA)
                .getJSONArray(TOKENS);
        if (jsonArray.isEmpty()) {
            throw new BusinessException(Constants.GET_TOKEN_ERROR_LOG);
        }
        return jsonArray.getJSONObject(0).getStr(TMP_TOKEN);
    }

    @Override
    public HttpResponse upload(File uploadFile, String uploadToken) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put(TMP_TOKEN, uploadToken);
        bodyMap.put(PERMISSION, 1);
        bodyMap.put(FILE, uploadFile);

        Map<String, String> headers = new HashMap<>();
        headers.put(Header.AUTHORIZATION.getValue(), BEARER + getUserToken());

        return executeUploadRequest(getUrl() + UPLOAD_FILE_PATH, headers, bodyMap);
    }

    @Override
    public String parseResponse(HttpResponse response) {
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        if (!jsonObject.getBool(STATUS)) {
            throw new BusinessException(jsonObject.getStr(MESSAGE));
        }
        return jsonObject.getJSONObject(DATA).getJSONObject(LINKS).getStr(URL);
    }

    @Override
    public String getType() {
        return "picui";
    }
}
