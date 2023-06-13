package com.jihai.bitfree.service;

import com.jihai.bitfree.dao.FileDAO;
import com.jihai.bitfree.dto.resp.FileUploadResp;
import com.jihai.bitfree.entity.FileDO;
import com.jihai.bitfree.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    @Autowired
    private FileDAO fileDAO;

    public String getUrlById(Long id) {
        FileDO fileDO = fileDAO.getUrlById(id);
        return fileDO == null ? "" : fileDO.getUrl();
    }

    public FileUploadResp uploadByMannal(String videoUrl, Long id) {
        FileDO fileDO = new FileDO();
        fileDO.setUserId(id);
        fileDO.setFormat(FileUploadUtils.getFormat(videoUrl));
        fileDO.setUrl(videoUrl);
        fileDO.setType(FileUploadUtils.convertFormat2Type(fileDO.getFormat()));

        fileDAO.insert(fileDO);

        FileUploadResp fileUploadResp = new FileUploadResp();
        fileUploadResp.setId(fileDO.getId());
        fileUploadResp.setUrl(videoUrl);
        return fileUploadResp;
    }
}
