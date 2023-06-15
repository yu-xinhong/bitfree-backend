package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.FileDO;

public interface FileDAO {

    void insert(FileDO fileDO);

    FileDO getById(Long id);
}
