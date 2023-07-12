package com.jihai.bitfree.dao;

import com.jihai.bitfree.dto.resp.RankPostItemResp;
import com.jihai.bitfree.entity.PostDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostDAO {

    Integer count(Long topicId);

    List<PostDO> pageQuery(@Param("start") Integer start, @Param("size") Integer size, @Param("topicId") Long topicId, String searchText, @Param("userId") Long userId);

    List<PostDO> queryByIdList(@Param("topPostIdList") List<Long> topPostIdList);

    PostDO getById(@Param("id") Long id);

    List<PostDO> getByIdList(@Param("idList") List<Long> idList);

    void insert(PostDO postDO);

    Integer countByUserId(@Param("userId") Long userId);

    void updateTime(@Param("postId") Long postId);

    List<RankPostItemResp> queryRankList();

    void deleted(@Param("postId") Long postId);

    void incrementView(@Param("id") Long id);

    List<PostDO> queryVideoList(@Param("start") Integer page, @Param("size") Integer size);

    int countVideo();
}
