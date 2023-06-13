package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.FileDO;

public interface FileDAO {

    void insert(FileDO fileDO);

    FileDO getUrlById(Long id);
}
