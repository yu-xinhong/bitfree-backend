package com.jihai.bitfree.demo;


import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class TransferDemo {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private DistributedLock distributedLock;

    // 案例1
    /*public void transfer(Long userId, Integer amount) throws InterruptedException {
        UserDO userDO = userDAO.getById(userId);
        if (userDO.getCoins() <= amount) return ;
        callAlipay(userDO, amount);
    }*/

    private void callAlipay(UserDO userDO, Integer amount) {
        userDAO.incrementCoins(userDO.getId(), - amount);
        System.out.println(userDO.getName() + " 提现 " + amount);
    }


    // 案例2
//    @Transactional
    public void transfer(Long userId, Integer amount) {
        UserDO userDO = userDAO.getById(userId);
        if (userDO.getCoins() < amount) return ;

        Boolean locked = distributedLock.lock("transfer", 10, TimeUnit.SECONDS);
        if (! locked) throw new BusinessException("业务繁忙，请稍后再试");

        try {
            userDO = userDAO.getById(userId);
            if (userDO.getCoins() < amount) return ;

            callAlipay(userDO, amount);
        } finally {
            distributedLock.unlock("transfer");
        }
    }
}
