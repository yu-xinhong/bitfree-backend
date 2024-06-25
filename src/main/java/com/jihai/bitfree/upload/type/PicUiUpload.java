package com.jihai.bitfree.upload.type;

import cn.hutool.http.HttpRequest;
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
 * <a href="https://picui.cn/user/dashboard">PicUi</a>
 */
@Slf4j
@Component
public class PicUiUpload extends UploadTemplate {

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
        String uploadToken = null;
        bodyMap.put("num", 1);
        bodyMap.put("seconds", 60);
        try (HttpResponse httpResponse = HttpRequest.post(getUrl() + "/images/tokens")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + userToken)
                .body(JSONUtil.toJsonStr(bodyMap), "application/json")
                .execute()) {
            String respJson = httpResponse.body();
            JSONArray jsonArray = JSONUtil.parseObj(respJson).getJSONObject("data")
                    .getJSONArray("tokens");
            if (!jsonArray.isEmpty()) {
                uploadToken = jsonArray.getJSONObject(0).getStr("tmpToken");
            }
        }
        return uploadToken;
    }


    public HttpResponse upload(File uploadFile, String uploadToken) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("file", uploadFile);
        bodyMap.put("tmpToken", uploadToken);
        bodyMap.put("permission", 1);
        return HttpRequest.post(getUrl() + "/upload")
                .header("Content-Type", "multipart/form-data")
                .header("Authorization", "Bearer " + getUserToken())
                .form(bodyMap)
                .execute();
    }

    @Override
    public String parseResponse(HttpResponse response) {
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        if (!jsonObject.getBool("status")) {
            throw new BusinessException(jsonObject.getStr("message"));
        }
        JSONObject data = jsonObject.getJSONObject("data");
        String linkUrl = data.getJSONObject("links").getStr("url");
        log.info("linkUrl: {}", linkUrl);
        return linkUrl;
    }

    @Override
    public String getType() {
        return "picui";
    }
}
