package com.jihai.bitfree.dto.req;

import com.jihai.bitfree.base.BaseReq;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class OrderDetailReq extends BaseReq {

    @NotNull
    private Long activityId;

    @NotNull
    @Length(max = 50)
    private String name;

    @NotNull
    @Length(max = 200)
    private String address;

//    @NotNull
    private String size;

//    @NotNull
    private String color;

    @NotNull
    private String tel;

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
