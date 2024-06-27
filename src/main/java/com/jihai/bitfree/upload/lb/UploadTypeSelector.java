package com.jihai.bitfree.upload.lb;

import java.util.List;

public interface UploadTypeSelector {

    /**
     * select upload type
     *
     * @param types all types
     * @return selected type
     */
    String select(List<String> types);

}
