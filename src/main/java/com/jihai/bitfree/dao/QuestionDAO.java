package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.QuestionDO;

import java.util.List;

public interface QuestionDAO {

    List<QuestionDO> getAll();

    void insert(QuestionDO questionDO);

    void deleteAll();

    QuestionDO getById(Long parentId);

    int updateStatus(Long id, Integer status);
}
