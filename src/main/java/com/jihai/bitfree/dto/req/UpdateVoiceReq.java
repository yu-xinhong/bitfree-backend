package com.jihai.bitfree.dto.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UpdateVoiceReq {
    @NotNull(message = "通知不能为空")
    @Max(1)
    @Min(0)
    private Integer voiceState;

    public Integer getVoiceState() {
        return voiceState;
    }

    public void setVoiceState(Integer voiceState) {
        this.voiceState = voiceState;
    }
}
