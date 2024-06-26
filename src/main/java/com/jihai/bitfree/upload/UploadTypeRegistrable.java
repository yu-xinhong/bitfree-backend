package com.jihai.bitfree.upload;

public interface UploadTypeRegistrable {

    /**
     * definition of current upload ability type
     *
     * @return upload ability type
     */
    String getType();

    /**
     * register current upload ability
     * <p>
     * type - upload ability
     */
    void registry();

}
