package com.jihai.bitfree.upload;

import java.io.File;

public interface UploadAbility {

    /**
     * upload file
     *
     * @param uploadFile the file to upload
     * @return file link url
     */
    String doUpload(File uploadFile);
}
