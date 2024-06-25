package com.jihai.bitfree.upload.lb;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinSelector implements UploadTypeSelector {

    /**
     * 当前选择的服务索引
     */
    private static final AtomicInteger CURR_INDEX = new AtomicInteger(0);

    @Override
    public String select(List<String> types) {
        if (types.isEmpty()) {
            return null;
        }

        // 只有一个类型，无需轮询
        int size = types.size();
        if (size == 1) {
            return types.get(0);
        }

        int index = CURR_INDEX.getAndIncrement() % size;
        return types.get(index);
    }
}
