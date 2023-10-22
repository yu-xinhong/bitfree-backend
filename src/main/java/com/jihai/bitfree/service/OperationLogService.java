package com.jihai.bitfree.service;

import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.constants.Constants;
import com.jihai.bitfree.dao.OperateLogDAO;
import com.jihai.bitfree.entity.OperateLogDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {

    @Autowired
    private OperateLogDAO operateLogDAO;

    @Async("commonAsyncThreadPool")
    public void toShortLink() {
        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setUserId(Constants.SYSTEM_DEFAULT_USER_ID);
        operateLogDO.setType(OperateTypeEnum.SHORT_LIN.getCode());
        operateLogDAO.insert(operateLogDO);
    }
}
