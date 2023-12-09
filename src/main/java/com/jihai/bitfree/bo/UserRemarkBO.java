package com.jihai.bitfree.bo;

public class UserRemarkBO {

    /**
     * 已读消息偏移量
     */
    private Long msgOffsetId;
    /**
     * 通知音开关状态 1:开启 0:关闭
     */
    private Integer voiceState;

    public Long getMsgOffsetId() {
        return msgOffsetId;
    }

    public void setMsgOffsetId(Long msgOffsetId) {
        this.msgOffsetId = msgOffsetId;
    }

    public Integer getVoiceState() {
        return voiceState;
    }

    public void setVoiceState(Integer voiceState) {
        this.voiceState = voiceState;
    }
}
