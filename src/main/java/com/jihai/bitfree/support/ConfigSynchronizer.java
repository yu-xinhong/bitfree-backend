package com.jihai.bitfree.support;

import com.jihai.bitfree.enums.ConfigEnum;
import com.jihai.bitfree.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置信息同步器, 用于集中化管理配置 避免配置变更带来的一些问题.
 */
@Component
public class ConfigSynchronizer {

    @Autowired
    private ConfigService configService;

    Set<ConfigEnum> synchronizations = EnumSet.of(ConfigEnum.SKIP_MONITOR_MESSAGE, ConfigEnum.TOP_POST_ID, ConfigEnum.SECRET);
    Map<ConfigEnum, String> resources = new ConcurrentHashMap<>(16);

    @PostConstruct
    private void initConfig(){
        refresh();
    }

    public void refresh(){
        for (ConfigEnum key : synchronizations){
            resources.put(key, configService.getByKey(key.toString()));
        }
    }


    public String get(ConfigEnum key){
        if (resources.containsKey(key))
            return resources.get(key);

        String config = configService.getByKey(key.toString());
        return resources.putIfAbsent(key, config);
    }


}
