package com.jihai.bitfree.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * XSS攻击配置类<br>
 * @author : Immortal Chengge
 * @Description : //TODO
 **/
@Data
@NoArgsConstructor
public class XssConfig {
    /**
     * 是否启用
     */
    private boolean enable = true;
    /**
     * 不过滤URL
     */
    private List<String> excludePaths = null;

    public XssConfig(boolean enable) {
        this.enable = enable;
    }
}
