package com.jihai.bitfree.service;

import com.jihai.bitfree.enums.OperateTypeEnum;
import com.jihai.bitfree.constants.LockKeyConstants;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.lock.LockTemplateSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class CoinsService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private LockTemplateSupport lockTemplateSupport;
    @Autowired
    private TransactionTemplate transactionTemplate;

    public void incrementCoins(Long userId, Integer coins, OperateTypeEnum operateTypeEnum) {
        String lockKey = LockKeyConstants.UPDATE_COINS + userId;
        lockTemplateSupport.lock(lockKey, 1, TimeUnit.MINUTES, () -> {
            UserDO userDO = userDAO.getById(userId);
            int afterCoins = userDO.getCoins() + coins;
            if (afterCoins < 0) {
                throw new BusinessException("硬币余额不足");
            }
            transactionTemplate.execute(action -> {
                operationLogService.saveCoinsOperateLog(userId, operateTypeEnum, coins, afterCoins);
                userDAO.incrementCoins(userId, coins);
                return true;
            });
        });
    }
}
