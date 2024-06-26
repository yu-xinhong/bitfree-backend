package com.jihai.bitfree.constants;

public interface Constants {

    String TOP_POST_ID = "TOP_POST_ID";
    String SECRET = "SECRET";
    String TOKEN = "token";
    String NOT_LOGIN = "请重新登录!";
    String EMAIL_SECRET = "MAIL_SECRET";
    String DEFAULT_PASSWORD_KEY = "DEFAULT_PASSWORD";
    String DEFAULT_POSTER = "DEFAULT_POSTER";
    String SENSITIVE_WORDS = "SENSITIVE_WORDS";
    String XSS_EXCLUDE_URLS = "XSS_EXCLUDE_URLS";
    String WEB_STATISTICS = "WEB_STATISTICS";
    String ROBOT_URL = "ROBOT_URL";
    String HEARTBEAT_SECRET = "MESSAGE_HEARTBEAT_SECRET";
    Long SYSTEM_DEFAULT_USER_ID = -1L;
    String MODIFY_SETTINGS_NOTIFICATION_ID = "MODIFY_SETTINGS_NOTIFICATION_ID";
    String TASK_COMPLETE_USER_LIST = "TASK_COMPLETE_USER_LIST";
    /**
     * 过滤历史操作记录的时间
     */
    String FILTER_OPERATION_HISTORY_TIME = "2023-12-22 16:00:00";
    String VERIFY_USER_LIST = "VERIFY_USER_LIST";
    String CHARGE_CONFIG = "CHARGE_CONFIG";

    String INVALID_FILE_NAME = "file name is invalid";
    int IMAGE_UPLOAD_RETRY_TIMES = 3;
    String IMAGE_UPLOAD_ERROR_LOG_TEMPLATE = "Upload failed with upload type: {}. Attempt: {}/{}. Error: {}";
    String DEFAULT_IMAGE_UPLOAD_TYPE = "picui";
    String GET_TOKEN_ERROR_LOG = "Get token failed";


    // region picture upload
    String PICUI_URL = "PICUI_URL";
    String PICUI_USER_TOKEN = "PICUI_USER_TOKEN";
    String XWYUE_URL = "XWYUE_URL";
    String XWYUE_USER_TOKEN = "XWYUE_USER_TOKEN";

    // endregion
}
