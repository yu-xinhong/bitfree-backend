package com.jihai.bitfree.support;

import com.jihai.bitfree.constants.LockKeyConstants;
import com.jihai.bitfree.dao.NotificationDAO;
import com.jihai.bitfree.entity.NotificationDO;
import com.jihai.bitfree.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Observable {

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private DistributedLock distributedLock;

    public void notify(Event event) {
        // 这里可以优化，现在if-else并不多
        if (event instanceof ReadNotificationEvent) {
            Boolean locked = distributedLock.lock(LockKeyConstants.SEND_NOTIFICATION, 1, TimeUnit.MINUTES);
            if (! locked) return ;

            try {
                ReadNotificationEvent readNotificationEvent = (ReadNotificationEvent) event;
                NotificationDO notificationDO = notificationDAO.getById(readNotificationEvent.getNotificationId());
                String newUserList = notificationDO.getUserList().replace("," + readNotificationEvent.getUserId(), "");

                notificationDAO.updateUserIdListById(notificationDO.getId(), newUserList);
            } finally {
                distributedLock.unlock(LockKeyConstants.SEND_NOTIFICATION);
            }
        }
    }
}
