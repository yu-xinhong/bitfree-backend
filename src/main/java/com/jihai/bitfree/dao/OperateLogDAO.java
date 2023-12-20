package com.jihai.bitfree.dao;

import com.jihai.bitfree.dto.req.GetCoinsRecordReq;
import com.jihai.bitfree.entity.OperateLogDO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface OperateLogDAO {
    void insert(OperateLogDO operateLogDO);

    Integer queryByUserIdAndType(@Param("userId") Long id,@Param("type") Integer type);

    Integer countRecentOpenChatLog(Long userId, Integer type);

    Integer countLoginRecord(Long userId, Integer type, Date date);

    List<OperateLogDO> queryByUserIdAndTypeList(Long userId, String filterHistoryTime, GetCoinsRecordReq req, Integer start, Integer size);

    Integer countByUserIdAndTypeList(Long userId, String filterHistoryTime, GetCoinsRecordReq req);
}
