package com.jihai.bitfree.dao;

import com.jihai.bitfree.entity.ConfigDO;

public interface ConfigDAO {

    ConfigDO getByKey(String key);

    void updateKey(String key, String value);

    void insert(ConfigDO config);
}
