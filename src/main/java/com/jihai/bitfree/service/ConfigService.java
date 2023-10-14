package com.jihai.bitfree.service;

import com.jihai.bitfree.dao.ConfigDAO;
import com.jihai.bitfree.entity.ConfigDO;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConfigService {

    @Autowired
    private ConfigDAO configDAO;

    public String getByKey(String key) {
        ConfigDO configDO = configDAO.getByKey(key);
        if (configDO == null) {
            log.error("key {} config not found", key);
            throw new BusinessException("not found config " + key);
        }
        return configDO.getValue();
    }
}
