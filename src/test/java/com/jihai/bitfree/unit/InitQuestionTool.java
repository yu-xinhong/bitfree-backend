package com.jihai.bitfree.unit;


import com.jihai.bitfree.dao.QuestionDAO;
import com.jihai.bitfree.entity.QuestionDO;
import com.jihai.bitfree.service.ConfigService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 本地解析markdown题库解析写入数据库
 */

public class InitQuestionTool extends AppTest {

    @Autowired
    private ConfigService configService;

    @Autowired
    private QuestionDAO questionDAO;

    @Test
    public void initQuestion() throws IOException {
        questionDAO.deleteAll();

        String localMarkDownPath = configService.getByKey("localMarkDownPath");
        String questionMarkDown = FileUtils.readFileToString(new File(localMarkDownPath));

        // 为了往前遍历，获取到第一个比当前等级高的节点即为父节点
        ArrayList<Node> nodeList = new ArrayList<>();
        readLine(questionMarkDown.split("\n"), 0, nodeList);
    }


    private void readLine(String[] questionMarkDownArray, int line, ArrayList<Node> nodeList) {
        if (questionMarkDownArray.length < line + 1) return ;
        String currentQuestion = questionMarkDownArray[line];
        String question = currentQuestion.replace(" ", "");
        if (StringUtils.isEmpty(question)) {
            readLine(questionMarkDownArray, line + 1, nodeList);
            return ;
        }
        int level = StringUtils.countOccurrencesOf(currentQuestion, "#");
        Node parentNode = null;
        int i = nodeList.size() - 1;
        while (i >= 0) {
            if (nodeList.get(i).getLevel() < level) {
                parentNode = nodeList.get(i);
                break;
            }
            i --;
        }
        Long parentId = parentNode != null ? parentNode.getId() : null;

        QuestionDO questionDO = new QuestionDO();
        questionDO.setLevel(level);
        questionDO.setContent(question.replace("#", ""));
        questionDO.setParentId(parentId);
        questionDAO.insert(questionDO);

        nodeList.add(new Node(questionDO.getId(), level));
        readLine(questionMarkDownArray, line + 1, nodeList);

    }

    static class Node {
        private Long id;
        private Integer level;

        public Node(Long id, Integer level) {
            this.id = id;
            this.level = level;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }
    }
}
