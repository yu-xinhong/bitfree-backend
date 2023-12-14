package com.jihai.bitfree.service.strategy;

import com.alibaba.fastjson.JSONObject;
import com.jihai.bitfree.dao.ActivityDAO;
import com.jihai.bitfree.dao.OrderDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.entity.ActivityDO;
import com.jihai.bitfree.entity.OrderDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class WeekendMeetingActivity extends Activity<BaseActivityParam> {

    private static final Integer TOP_USER_COINS = 32;
    private static final Integer NOT_TOP_USER_COINS = 16;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private OperationLogService operationLogService;

    @Override
    public boolean support(ActivityTypeEnum activityTypeEnum) {
        return ActivityTypeEnum.MEETING.equals(activityTypeEnum);
    }

    /**
     * 1. 排行TOP10 直接参加，扣硬币32
     * 2. 排行非TOP10 抢购，扣16，库存锁定
     * @param param
     * @return
     */
    @Override
    @Transactional
    public boolean doKill(BaseActivityParam param) {
        List<UserDO> userDOList = userDAO.getRanksByCoins();
        if (userDOList.stream().anyMatch(e -> e.getId().equals(param.getUserId()))) {
            if (userDAO.incrementCoins(param.getUserId(), - TOP_USER_COINS) == 1) throw new BusinessException("硬币不足");
        } else {
            if (activityDAO.updateStock(param.getActivityId(), 1) == 0) {
                throw new BusinessException("名额不足");
            }
            if (userDAO.incrementCoins(param.getUserId(), - NOT_TOP_USER_COINS) == 0) {
                throw new BusinessException("硬币不足");
            }
        }


        ActivityDO activityDO = activityDAO.queryActivity(param.getActivityId());
        OrderDO orderDO = new OrderDO();
        orderDO.setActivityId(param.getActivityId());
        orderDO.setUserId(param.getUserId());
        orderDO.setCount(1);
        orderDO.setDetail(JSONObject.parseObject(activityDO.getDetail()).getString("detail"));
        orderDAO.insert(orderDO);

        return true;
    }
}
