package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.FileDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileDAO {

    void insert(FileDO fileDO);

    FileDO getById(Long id);

    List<FileDO> batchQueryById(@Param("fileIdList") List<Long> fileIdList);
}
