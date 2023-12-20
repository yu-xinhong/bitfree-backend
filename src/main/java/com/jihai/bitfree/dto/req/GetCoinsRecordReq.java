package com.jihai.bitfree.dto.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GetCoinsRecordReq {
    @Min(0)
    @Max(100)
    @NotNull(message = "页码不传的吗")
    private Integer page = 1;

    @Min(0)
    @Max(1000)
    @NotNull(message = "每页size不传的吗")
    private Integer size = 20;

    private String startTime;

    private String endTime;

    private List<Integer> typeList;
}
