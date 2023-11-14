package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.MessageDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageDAO {

    List<MessageDO> pageQueryRecentList(Integer start, Integer size);

    Integer count();

    Integer insert(MessageDO messageDO);

    Integer delete(@Param("id") Long id);

    Integer getRecentMessageCount();

    Integer countAfterId(Long userId, Long id);

    Long getRecentMessageId();

    Long getSendUserIdByMessageId(Long id);

    List<MessageDO> queryByTargetMessageIdList(List<Long> notificationIdList);


}
