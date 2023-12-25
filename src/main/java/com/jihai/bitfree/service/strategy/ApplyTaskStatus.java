package com.jihai.bitfree.service.strategy;

import cn.hutool.core.util.ObjUtil;
import com.jihai.bitfree.dao.TaskBoardDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.entity.TaskBoardDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.enums.TaskStatusEnum;
import com.jihai.bitfree.enums.UserLevelEnum;
import com.jihai.bitfree.exception.BusinessException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Component
public class ApplyTaskStatus extends TaskBoardStatus<BaseTaskBoardParam>{

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TaskBoardDAO taskBoardDAO;

    public ApplyTaskStatus() {
        super(TaskStatusEnum.TODO, TaskStatusEnum.DOING);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = READ_COMMITTED)
    public boolean doChange(BaseTaskBoardParam param) {
        UserDO userDO = userDAO.getById(param.getUserId());
        if(! UserLevelEnum.ULTIMATE.getLevel().equals(userDO.getLevel())){
            throw new BusinessException("请升级旗舰版！");
        }
        List<TaskBoardDO> taskByTaskUserList = taskBoardDAO.getTaskByTaskUserId(param.getUserId(), TaskStatusEnum.DOING.getStatus());
        if (ObjectUtils.isNotEmpty(taskByTaskUserList) && taskByTaskUserList.size() >= 3) {
            throw new BusinessException("您处理中的任务大于3个,请尽快完成后再申领噢～");
        }

        int updateColumn = taskBoardDAO.updateByIdAndStatus(param.getTaskId(), param.getUserId(), beforeStatus().getStatus(), supportStatus().getStatus());
        return updateColumn == 1;
    }
}
