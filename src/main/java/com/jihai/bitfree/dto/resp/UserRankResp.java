package com.jihai.bitfree.dto.resp;

import com.jihai.bitfree.base.BaseResp;

import java.util.List;

public class UserRankResp extends BaseResp {

    private Long rank;

    private List<UserResp> list;

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public List<UserResp> getList() {
        return list;
    }

    public void setList(List<UserResp> list) {
        this.list = list;
    }
}
