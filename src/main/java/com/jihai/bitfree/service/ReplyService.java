package com.jihai.bitfree.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.dao.ReplyDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.ReplyListDTO;
import com.jihai.bitfree.entity.ReplyDO;
import com.jihai.bitfree.entity.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReplyService {

    @Autowired
    private ReplyDAO replyDAO;

    @Autowired
    private UserDAO userDAO;

    public List<ReplyListDTO> getReplyList(Long id) {
        List<ReplyListDTO> replyListDTOS = Lists.newArrayList();
        List<ReplyDO> replyDOList = replyDAO.getByPostId(id);
        if (CollectionUtils.isEmpty(replyDOList)) {
            return replyListDTOS;
        }

        List<Long> sendUserIdList = replyDOList.stream().map(ReplyDO::getSendUserId).collect(Collectors.toList());
        List<UserDO> userDOS = userDAO.batchQueryByIdList(sendUserIdList);

        ImmutableMap<Long, UserDO> idUserMap = Maps.uniqueIndex(userDOS, UserDO::getId);

        // 获取主评论
        List<ReplyDO> mainReplyList = replyDOList.stream().filter(replyDO -> replyDO.getTargetReplyId() == null).collect(Collectors.toList());

        // 获取子评论
        List<ReplyDO> subReplyList = replyDOList.stream().filter(replyDO -> replyDO.getTargetReplyId() != null).collect(Collectors.toList());

        return mainReplyList.stream().map(mainReply -> {

            ReplyListDTO replyListDTO = convertReply2DTO(mainReply, idUserMap);

            // 填充子评论
            List<ReplyDO> currentSubReplyList = subReplyList.stream().filter(subReply -> subReply.getTargetReplyId().equals(mainReply.getId())).collect(Collectors.toList());
            if (! CollectionUtils.isEmpty(currentSubReplyList)) {
                List<ReplyListDTO> subReplys = currentSubReplyList.stream().map(replyDO -> convertReply2DTO(replyDO, idUserMap)).collect(Collectors.toList());
                replyListDTO.setSubReplyList(subReplys);
            }
            return replyListDTO;
        }).collect(Collectors.toList());
    }

    private ReplyListDTO convertReply2DTO(ReplyDO mainReply, ImmutableMap<Long, UserDO> idUserMap) {
        ReplyListDTO replyListDTO = new ReplyListDTO();

        replyListDTO.setId(mainReply.getId());
        replyListDTO.setReplyContent(mainReply.getReplyContent());
        replyListDTO.setCreateTime(mainReply.getCreateTime());
        replyListDTO.setCreatorId(mainReply.getSendUserId());

        replyListDTO.setName(idUserMap.get(mainReply.getSendUserId()).getName());

        return replyListDTO;
    }
}
