package com.jihai.bitfree.utils;

import com.google.common.collect.Lists;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class FileUploadUtils {

    static List<String> imageFormatList = Lists.newArrayList("png", "jpeg", "jpg");

    static List<String> videoFormatList = Lists.newArrayList("mp4", "mov", "mp3");

    public final static Integer IMAGE_TYPE = 2;
    public final static Integer VIDEO_TYPE = 1;


    public static String getFormat(String originalFilename) {
        if (StringUtils.isEmpty(originalFilename)) return "";
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1, originalFilename.indexOf("?") == -1 ? originalFilename.length() : originalFilename.indexOf("?"));
    }

    public static Integer convertFormat2Type(String format) {
        if (imageFormatList.contains(format.toLowerCase())) return IMAGE_TYPE;
        if (videoFormatList.contains(format.toLowerCase())) return VIDEO_TYPE;
        log.error("not support file format {} ", format);
        throw new BusinessException("不支持文件类型");
    }
}
