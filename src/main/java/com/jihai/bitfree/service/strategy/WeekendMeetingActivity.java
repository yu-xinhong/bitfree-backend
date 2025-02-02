package com.jihai.bitfree.service.strategy;

import com.alibaba.fastjson.JSONObject;
import com.jihai.bitfree.enums.ActivityTypeEnum;
import com.jihai.bitfree.enums.OperateTypeEnum;
import com.jihai.bitfree.enums.UserLevelEnum;
import com.jihai.bitfree.dao.ActivityDAO;
import com.jihai.bitfree.dao.OrderDAO;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.entity.ActivityDO;
import com.jihai.bitfree.entity.OrderDO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.service.CoinsService;
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
    @Autowired
    private CoinsService coinsService;

    @Override
    public boolean support(ActivityTypeEnum activityTypeEnum) {
        return ActivityTypeEnum.MEETING.equals(activityTypeEnum);
    }

    /**
     * 1. 排行TOP10 直接参加，扣硬币32
     * 2. 排行非TOP10 抢购，扣16，库存锁定
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public boolean doKill(BaseActivityParam param) {
        if (!UserLevelEnum.ULTIMATE.getLevel().equals(userDAO.getUserLevelById(param.getUserId())))
            throw new BusinessException("旗舰用户才能参加周会");
        List<UserDO> userDOList = userDAO.getRanksByCoins();
        int coins = 0;
        if (userDOList.stream().anyMatch(e -> e.getId().equals(param.getUserId()))) {
            coinsService.incrementCoins(param.getUserId(), -(coins = TOP_USER_COINS), OperateTypeEnum.ACTIVITY);
        } else {
            if (activityDAO.updateStock(param.getActivityId(), 1) == 0) {
                throw new BusinessException("名额不足");
            }
            coinsService.incrementCoins(param.getUserId(), -(coins = NOT_TOP_USER_COINS), OperateTypeEnum.ACTIVITY);
        }


        ActivityDO activityDO = activityDAO.queryActivity(param.getActivityId());
        OrderDO orderDO = new OrderDO();
        orderDO.setActivityId(param.getActivityId());
        orderDO.setUserId(param.getUserId());
        orderDO.setCount(1);
        orderDO.setCoins(coins);
        orderDO.setDetail(JSONObject.parseObject(activityDO.getDetail()).getString("detail"));
        orderDAO.insert(orderDO);

        return true;
    }
}
