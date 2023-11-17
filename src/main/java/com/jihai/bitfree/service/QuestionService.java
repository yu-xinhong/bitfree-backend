package com.jihai.bitfree.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jihai.bitfree.dao.QuestionDAO;
import com.jihai.bitfree.dto.resp.QuestionNodeResp;
import com.jihai.bitfree.entity.QuestionDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionDAO questionDAO;

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
}
