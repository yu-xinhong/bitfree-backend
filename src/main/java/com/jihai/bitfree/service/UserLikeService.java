package com.jihai.bitfree.service;

import com.jihai.bitfree.base.enums.LikeTypeEnum;
import com.jihai.bitfree.dao.UserLikeDAO;
import com.jihai.bitfree.dto.resp.ReplyListResp;
import com.jihai.bitfree.entity.UserLikeDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLikeService {

    @Autowired
    private UserLikeDAO userLikeDAO;

    public void fillUserLike(List<ReplyListResp> replyList, Long userId) {
        List<Long> targetIdList = replyList.stream().map(ReplyListResp::getId).distinct().collect(Collectors.toList());
        List<UserLikeDO> likeDOList = userLikeDAO.getLikeList(targetIdList, LikeTypeEnum.REPLY.getType(), userId);
        if (CollectionUtils.isEmpty(likeDOList)) return ;
        replyList.forEach(replyListResp -> {
            replyListResp.setLike(likeDOList.stream().anyMatch(e -> e.getTargetId().equals(replyListResp.getId())));
        });
    }
}
