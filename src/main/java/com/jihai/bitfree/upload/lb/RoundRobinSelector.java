package com.jihai.bitfree.upload.lb;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class RoundRobinSelector implements UploadTypeSelector {

    private static final LongAdder CURR_INDEX = new LongAdder();

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

        long currentIndex = CURR_INDEX.longValue();
        int index = (int) (currentIndex % size);
        CURR_INDEX.increment();

        return types.get(index);
    }
}
