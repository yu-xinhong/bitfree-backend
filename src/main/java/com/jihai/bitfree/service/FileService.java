package com.jihai.bitfree.service;

import com.jihai.bitfree.dao.FileDAO;
import com.jihai.bitfree.dto.resp.FileUploadResp;
import com.jihai.bitfree.entity.FileDO;
import com.jihai.bitfree.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Autowired
    private FileDAO fileDAO;

    public FileUploadResp upload(MultipartFile multipartFile, Long userId) {
        // upload cdn
        String url = uploadCDN(multipartFile);

        FileDO fileDO = new FileDO();
        fileDO.setUserId(userId);
        fileDO.setName(multipartFile.getName());
        fileDO.setFormat(FileUploadUtils.getFormat(multipartFile.getOriginalFilename()));
        fileDO.setUrl(url);
        fileDO.setType(FileUploadUtils.convertFormat2Type(fileDO.getFormat()));

        fileDAO.insert(fileDO);

        FileUploadResp fileUploadResp = new FileUploadResp();
        fileUploadResp.setId(fileDO.getId());
        fileUploadResp.setUrl(url);
        return fileUploadResp;
    }

    private String uploadCDN(MultipartFile multipartFile) {
        return "https://vjs.zencdn.net/v/oceans.mp4";
    }

    public String getUrlById(Long id) {
        FileDO fileDO = fileDAO.getUrlById(id);
        return fileDO == null ? "" : fileDO.getUrl();
    }
}
