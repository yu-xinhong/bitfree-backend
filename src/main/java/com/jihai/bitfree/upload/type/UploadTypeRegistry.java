package com.jihai.bitfree.upload.type;

import cn.hutool.core.util.StrUtil;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.upload.UploadAbility;
import java.util.ArrayList;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class UploadTypeRegistry {

    private static final Map<String, UploadAbility> UPLOAD_TYPE_MAP = new HashMap<>();

    public static void register(String type, UploadAbility uploadAbility) {
        UPLOAD_TYPE_MAP.put(type, uploadAbility);
    }

    public static List<String> getTypes() {
        return new ArrayList<>(UPLOAD_TYPE_MAP.keySet());
    }

    public static UploadAbility resolve(String type) {
        if (StrUtil.isBlank(type)) {
            return UPLOAD_TYPE_MAP.get("picui");
        }
        UploadAbility uploadAbility = UPLOAD_TYPE_MAP.get(type);
        if (uploadAbility == null) {
            throw new BusinessException("不支持的图床上传类型");
        }
        return uploadAbility;
    }
}
