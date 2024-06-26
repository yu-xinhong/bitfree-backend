package com.jihai.bitfree.upload.type;

import cn.hutool.core.util.StrUtil;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.upload.UploadAbility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

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
        UploadAbility defaultUploadAbility
                = UPLOAD_TYPE_MAP.get(Constants.DEFAULT_IMAGE_UPLOAD_TYPE);
        if (StrUtil.isBlank(type)) {
            return defaultUploadAbility;
        }
        UploadAbility uploadAbility = UPLOAD_TYPE_MAP.get(type);
        if (uploadAbility == null) {
            return defaultUploadAbility;
        }
        return uploadAbility;
    }
}
