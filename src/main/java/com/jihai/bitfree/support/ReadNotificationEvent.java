package com.jihai.bitfree.support;

public class ReadNotificationEvent implements Event {

    private Long userId;

    private Long notificationId;

    public ReadNotificationEvent(Long userId, Long notificationId) {
        this.userId = userId;
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
}
