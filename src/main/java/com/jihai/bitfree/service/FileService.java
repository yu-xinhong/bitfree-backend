package com.jihai.bitfree.service;

import com.jihai.bitfree.dao.FileDAO;
import com.jihai.bitfree.dto.resp.FileUploadResp;
import com.jihai.bitfree.dto.resp.GetFileResp;
import com.jihai.bitfree.entity.FileDO;
import com.jihai.bitfree.utils.FileUploadUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    @Autowired
    private FileDAO fileDAO;

    public FileUploadResp uploadByMannal(String videoUrl, String poster, Long userId) {
        FileDO fileDO = new FileDO();
        fileDO.setUserId(userId);
        fileDO.setFormat(FileUploadUtils.getFormat(videoUrl));
        fileDO.setUrl(videoUrl);
        fileDO.setType(FileUploadUtils.convertFormat2Type(fileDO.getFormat()));
        fileDO.setPoster(poster);

        fileDAO.insert(fileDO);

        FileUploadResp fileUploadResp = new FileUploadResp();
        fileUploadResp.setId(fileDO.getId());
        fileUploadResp.setUrl(videoUrl);
        return fileUploadResp;
    }

    public GetFileResp getById(Long id) {
        FileDO fileDO = fileDAO.getById(id);
        GetFileResp getFileResp = new GetFileResp();
        BeanUtils.copyProperties(fileDO, getFileResp);
        return getFileResp;
    }
}
