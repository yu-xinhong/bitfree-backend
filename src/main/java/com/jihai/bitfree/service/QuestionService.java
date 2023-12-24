package com.jihai.bitfree.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.constants.CoinsDefinitions;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.QuestionDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.dto.resp.QuestionNodeResp;
import com.jihai.bitfree.entity.QuestionDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.enums.OperateTypeEnum;
import com.jihai.bitfree.enums.QuestionStatusEnum;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.utils.StringListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private ConfigService configService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private UserDAO userDAO;

    public List<QuestionNodeResp> getTree() {
        List<QuestionDO> questionDOList = questionDAO.getAll();
        if (CollectionUtils.isEmpty(questionDOList)) return Collections.emptyList();
        return buildTree(questionDOList);
    }

    private List<QuestionNodeResp> buildTree(List<QuestionDO> questionDOList) {
        // 构造树结构
        Integer maxLevel = questionDOList.stream().mapToInt(QuestionDO::getLevel).max().getAsInt();

        // 按照level排序
        Map<Integer, List<QuestionDO>> levelQuestionMap = questionDOList.stream().collect(Collectors.groupingBy(QuestionDO::getLevel));
        List<QuestionNodeResp> resultList = Lists.newArrayList();
        Map<Long, QuestionNodeResp> idQuestionNodeMap = Maps.newHashMap();
        dfs(levelQuestionMap, 1, maxLevel, questionDOList, resultList, idQuestionNodeMap);
        return resultList;
    }

    private void dfs(Map<Integer, List<QuestionDO>> levelQuestionMap, Integer level, Integer maxLevel, List<QuestionDO> questionDOList, List<QuestionNodeResp> resultList, Map<Long, QuestionNodeResp> idQuestionNodeMap) {
        // 递归出口
        if (level > maxLevel) return;

        List<QuestionDO> currentLevelQuestionList = questionDOList.stream().filter(e -> e.getLevel().equals(level)).collect(Collectors.toList());
        // 根节点
        if (level.equals(1)) {
            List<QuestionNodeResp> rootQuestionNodeRespList = currentLevelQuestionList.stream().map(e -> convert2QuestionResp(e)).collect(Collectors.toList());
            resultList.addAll(rootQuestionNodeRespList);
            idQuestionNodeMap.putAll(Maps.uniqueIndex(resultList, QuestionNodeResp::getId));
            dfs(levelQuestionMap, level + 1, maxLevel, questionDOList, resultList, idQuestionNodeMap);
            return ;
        }

        // 子节点
        Integer parentId = level - 1;
        List<QuestionDO> parentQuestionList = levelQuestionMap.get(parentId);
        parentQuestionList.forEach(parentQuestion -> {
            List<QuestionDO> subQuestionList = currentLevelQuestionList.stream().filter(currentLevelQuestion -> currentLevelQuestion.getParentId().equals(parentQuestion.getId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(subQuestionList)) return ;

            List<QuestionNodeResp> subQuestionRespList = subQuestionList.stream().map(e -> convert2QuestionResp(e)).collect(Collectors.toList());
            idQuestionNodeMap.putAll(Maps.uniqueIndex(subQuestionRespList, QuestionNodeResp::getId));
            idQuestionNodeMap.get(parentQuestion.getId()).setSubTreeNodeResp(subQuestionRespList);
        });
        dfs(levelQuestionMap, level + 1, maxLevel, questionDOList, resultList, idQuestionNodeMap);
    }

    private QuestionNodeResp convert2QuestionResp(QuestionDO questionDO) {
        QuestionNodeResp questionNodeResp = new QuestionNodeResp();
        BeanUtils.copyProperties(questionDO, questionNodeResp);
        return questionNodeResp;
    }

    public Boolean addNode(Long parentId, String content, Long userId) {
        QuestionDO parentNode = questionDAO.getById(parentId);
        QuestionDO questionDO = new QuestionDO();
        questionDO.setParentId(parentId);
        questionDO.setLevel(parentNode.getLevel() + 1);
        questionDO.setContent(content);
        questionDO.setStatus(QuestionStatusEnum.COMMITTED.getStatus());
        questionDO.setUserId(userId);

        questionDAO.insert(questionDO);
        return true;
    }

    @Transactional
    public Boolean verify(Long nodeId, Integer status, Long userId) {
        List<String> canVerifyUserIdList = StringListUtils.str2List(configService.getByKey(Constants.VERIFY_USER_LIST));
        if (CollectionUtils.isEmpty(canVerifyUserIdList)) throw new BusinessException("可审核人为空");

        if (canVerifyUserIdList.stream().noneMatch(e -> e.equals(userId.toString()))) throw new BusinessException("无审核权限");

        QuestionDO questionDO = questionDAO.getById(nodeId);
        if (! QuestionStatusEnum.COMMITTED.getStatus().equals(questionDO.getStatus())) throw new BusinessException("无法审核");

        questionDAO.updateStatus(questionDO.getId(), status);
        if (QuestionStatusEnum.VERIFIED.getStatus().equals(status)) {
            // 奖励硬币
            userDAO.incrementCoins(questionDO.getUserId(), CoinsDefinitions.COMMITTED_QUESTION_COINS);
            UserDO userDO = userDAO.getById(questionDO.getUserId());
            operationLogService.saveCoinsOperateLog(userId, OperateTypeEnum.COMMITTED_QUESTION, CoinsDefinitions.COMMITTED_QUESTION_COINS, userDO.getCoins());
        }
        return true;
    }
}
