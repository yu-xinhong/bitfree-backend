package com.jihai.bitfree.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jihai.bitfree.enums.OperateTypeEnum;
import com.jihai.bitfree.constants.LockKeyConstants;
import com.jihai.bitfree.dao.UserDAO;
import com.jihai.bitfree.entity.UserDO;
import com.jihai.bitfree.exception.BusinessException;
import com.jihai.bitfree.lock.LockTemplateSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.Map.Entry;

import static java.util.Map.Entry.comparingByValue;

@Service
public class CoinsService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private LockTemplateSupport lockTemplateSupport;
    @Autowired
    private TransactionTemplate transactionTemplate;

    // 创建一个top10缓存
    // 键为用户id，值为硬币数
    // 此缓存存的是前10名用户以及他们的硬币数
    private final Cache<Long, Integer> topCache = CacheBuilder.newBuilder().maximumSize(20).build();

    // 计算指定用户为top几
    public Integer topCounter(Long userId) {
        return topCache.getIfPresent(userId) != null
                ? topCache.asMap()
                .entrySet()
                .stream()
                .sorted(comparingByValue(Comparator.reverseOrder()))
                .map(Entry::getKey)
                .collect(Collectors.toList())
                .indexOf(userId) + 1
                : -1;
    }

    public List<Long> getTopUserIds() {
        return new ArrayList<>(topCache.asMap().keySet());
    }

    // 初始化这个top10缓存，查出top10
    @PostConstruct
    public void init() {
        // 迭代放入缓存中
        List<UserDO> userDOS = userDAO.checkCoinNumber();
        for (UserDO userDO : userDOS) {
            topCache.put(userDO.getId(), userDO.getCoins());
        }
    }

    public void incrementCoins(Long userId, Integer coins, OperateTypeEnum operateTypeEnum) {
        String lockKey = LockKeyConstants.UPDATE_COINS + userId;
        lockTemplateSupport.lock(lockKey, 1, TimeUnit.MINUTES, () -> {
            UserDO userDO = userDAO.getById(userId);
            int afterCoins = userDO.getCoins() + coins;
            if (afterCoins < 0) {
                throw new BusinessException("硬币余额不足");
            }
            transactionTemplate.execute(action -> {
                operationLogService.saveCoinsOperateLog(userId, operateTypeEnum, coins, afterCoins);
                userDAO.incrementCoins(userId, coins);
                return true;
            });
            // 如果需要增减硬币的用户已在top10
            if (topCache.getIfPresent(userId) != null) {
                topCache.put(userId, afterCoins);
            } else {
                // 如果不在缓存中
                // 如果缓存没满，则直接加入返回
                if (topCache.size() < 10) {
                    topCache.put(userId, afterCoins);
                    return;
                }
                // 否则，缓存已满
                // 筛选出缓存中硬币数最小的用户
                Entry<Long, Integer> leastCoinsUser = topCache.asMap()
                        .entrySet()
                        .stream()
                        .min(comparingByValue())
                        .orElse(null);
                // 如果这个用户的硬币数小于当前用户的加完之后的硬币数
                if (leastCoinsUser.getValue() < afterCoins) {
                    // 剔除这个用户，加入新用户
                    topCache.invalidate(leastCoinsUser.getKey());
                    topCache.put(userId, afterCoins);
                }
            }
        });
    }
}
