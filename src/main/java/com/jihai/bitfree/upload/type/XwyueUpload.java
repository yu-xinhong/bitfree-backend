package com.jihai.bitfree.upload.type;

import cn.hutool.http.HttpRequest;
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


    @Override
    public String getUrl() {
        // return "https://img.xwyue.com/api/v1";
        return configService.getByKey(Constants.XWYUE_URL);
    }

    @Override
    public String getUserToken() {
        String userToken = configService.getByKey(Constants.XWYUE_USER_TOKEN);
        String[] tokens = userToken.split(" ");
        String email = tokens[0];
        String password = tokens[1];

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("email", email);
        bodyMap.put("password", password);
        try (HttpResponse httpResponse = HttpRequest.post(getUrl() + "/tokens")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(bodyMap))
                .execute()) {
            JSONObject obj = JSONUtil.parseObj(httpResponse.body());
            if (obj.getBool("status")) {
                return obj.getJSONObject("data").getStr("token");
            }
        }
        throw new BusinessException("获取token失败");
    }

    @Override
    public String getUploadToken(String userToken) {
        return userToken;
    }

    @Override
    public HttpResponse upload(File uploadFile, String uploadToken) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("file", uploadFile);
        return HttpRequest.post(getUrl() + "/upload")
                .header("Content-Type", "multipart/form-data")
                .header("Authorization", "Bearer " + uploadToken)
                .form(bodyMap)
                .execute();
    }

    @Override
    public String parseResponse(HttpResponse response) {
        JSONObject obj = JSONUtil.parseObj(response.body());
        if (!obj.getBool("status")) {
            throw new BusinessException(obj.getStr("message"));
        }
        JSONObject data = obj.getJSONObject("data");
        String url = data.getJSONObject("links").getStr("url");
        log.info("linkUrl: {}", url);
        return url;
    }

    @Override
    public String getType() {
        return "xwyue";
    }
}
