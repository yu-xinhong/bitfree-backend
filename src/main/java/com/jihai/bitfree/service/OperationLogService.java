package com.jihai.bitfree.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.jihai.bitfree.base.PageResult;
import com.jihai.bitfree.base.enums.OperateTypeEnum;
import com.jihai.bitfree.bo.OperateRemarkBO;
import com.jihai.bitfree.dao.OperateLogDAO;
import com.jihai.bitfree.dto.req.GetCoinsRecordReq;
import com.jihai.bitfree.dto.resp.CoinsRecordTypeResp;
import com.jihai.bitfree.dto.resp.OperationResp;
import com.jihai.bitfree.entity.OperateLogDO;
import com.jihai.bitfree.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OperationLogService {

    @Autowired
    private OperateLogDAO operateLogDAO;

    private List<Integer> coinsOperTypeList;

    private List<OperateTypeEnum> coinsOperEnumList;

    @PostConstruct
    private void init(){
        coinsOperEnumList = Arrays.stream(OperateTypeEnum.values()).flatMap(typeEnum -> {
            if (typeEnum.getCoinCorrelation()) {
                return Stream.of(typeEnum);
            }
            return null;
        }).collect(Collectors.toList());
        coinsOperTypeList = coinsOperEnumList.stream().map(OperateTypeEnum::getCode).collect(Collectors.toList());
    }


    @Async("commonAsyncThreadPool")
    public void asynSaveOperateLog(Long userId, OperateTypeEnum operateTypeEnum) {
        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setUserId(userId);
        operateLogDO.setType(operateTypeEnum.getCode());
        operateLogDAO.insert(operateLogDO);
    }

    @Async("commonAsyncThreadPool")
    public void asynSaveOperateLog(Long userId, OperateTypeEnum operateTypeEnum, Integer coins, Integer afterCoins) {
        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setUserId(userId);
        operateLogDO.setType(operateTypeEnum.getCode());
        OperateRemarkBO operateRemarkBo = new OperateRemarkBO();
        operateRemarkBo.setCoins(coins);
        operateRemarkBo.setAfterCoins(afterCoins);
        operateLogDO.setRemark(JSON.toJSONString(operateRemarkBo));
        operateLogDAO.insert(operateLogDO);
    }

    public PageResult<OperationResp> getCoinsRecord(Long userId, GetCoinsRecordReq req) {
        if (CollUtil.isEmpty(req.getTypeList())){
            req.setTypeList(coinsOperTypeList);
        }else {
            // 校验类型是否越权
            for (Integer type : req.getTypeList()) {
                if (! coinsOperTypeList.contains(type)) {
                    throw new BusinessException("非法请求参数");
                }
            }
        }
        List<OperateLogDO> operateLogDOList = this.queryByUserIdAndTypeList(userId, req);
        List<OperationResp> operationRespList = new ArrayList<>();
        OperationResp operationResp;
        for (OperateLogDO operateLogDO : operateLogDOList) {
            operationResp = new OperationResp();
            operationResp.setUserId(operateLogDO.getUserId());
            operationResp.setType(operateLogDO.getType());
            Optional<OperateTypeEnum> operateTypeOptional = coinsOperEnumList.stream().filter(v -> Objects.equals(v.getCode(), operateLogDO.getType())).findFirst();
            operationResp.setTypeDesc(operateTypeOptional.map(OperateTypeEnum::getDesc).orElse(null));
            operationResp.setCreateTime(operateLogDO.getCreateTime());
            if (StrUtil.isNotBlank(operateLogDO.getRemark())) {
                OperateRemarkBO operateRemarkBO = JSON.parseObject(operateLogDO.getRemark(), OperateRemarkBO.class);
                operationResp.setCoins(operateRemarkBO.getCoins());
                operationResp.setAfterCoins(operateRemarkBO.getAfterCoins());
            }
            operationRespList.add(operationResp);
        }
        Integer count = operateLogDAO.countByUserIdAndTypeList(userId, req);
        return new PageResult<>(operationRespList, count);
    }

    public List<OperateLogDO> queryByUserIdAndTypeList(Long userId, GetCoinsRecordReq req) {
        return operateLogDAO.queryByUserIdAndTypeList(userId, req, (req.getPage() - 1) * req.getSize(), req.getSize());
    }

    public List<CoinsRecordTypeResp> getCoinsTypeList() {
        List<CoinsRecordTypeResp> recordTypeRespList = new ArrayList<>();
        CoinsRecordTypeResp coinsRecordTypeResp;
        for (OperateTypeEnum operateTypeEnum : coinsOperEnumList) {
            coinsRecordTypeResp = new CoinsRecordTypeResp();
            coinsRecordTypeResp.setCode(operateTypeEnum.getCode());
            coinsRecordTypeResp.setDesc(operateTypeEnum.getDesc());
            recordTypeRespList.add(coinsRecordTypeResp);
        }
        return recordTypeRespList;
    }
}
