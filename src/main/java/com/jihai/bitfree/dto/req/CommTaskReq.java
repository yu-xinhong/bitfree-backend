package com.jihai.bitfree.dto.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommTaskReq {
    @NotNull(message = "任务编号不能为空")
    private Integer taskId;
}
