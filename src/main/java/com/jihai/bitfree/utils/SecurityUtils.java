package com.jihai.bitfree.utils;

import com.jihai.bitfree.base.enums.ReturnCodeEnum;
import com.jihai.bitfree.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import java.util.regex.Pattern;

@Slf4j
public class SecurityUtils {
    /**
     * sql注入正则
     */
    private static final String badStrReg =
            "\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";

    /**
     * xss脚本正则
     */
    private final static Pattern[] scriptPatterns = {
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    /**
     * 清除xss
     * @param src 单个参数
     * @return cleanedSrc -被清洗过的原文
     */
    public static String cleanXSS(String src) {
        String temp = src;
        // 校验xss脚本
        for (Pattern pattern : scriptPatterns) {
            temp = pattern.matcher(temp).replaceAll("");
        }
        // 校验xss特殊字符，太严了先注掉了
//        temp = temp.replaceAll("\0|\n|\r", "");
//        temp = temp.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        if (!temp.equals(src)) {

            log.error("xss攻击检查：参数含有非法攻击字符，已禁止继续访问！！");
            log.error("原始输入信息-->" + src);
            log.error("[debug]过滤后信息-->" + temp);
            throw new BusinessException(ReturnCodeEnum.ILLEGAL_CHARACTERS_ERROR);
        }

        return src;
    }

    /**
     * 过滤sql注入 -- 需要增加通配，过滤大小写组合
     * @param src 单个参数值
     * @return src 原文
     * @throws BusinessException 通用业务异常
     */
    public static String cleanSQLInject(String src) {
        // 非法sql注入正则
        Pattern sqlPattern = Pattern.compile(badStrReg, Pattern.CASE_INSENSITIVE);
        if (sqlPattern.matcher(src.toLowerCase()).find()) {
            log.error("sql注入检查：输入信息存在SQL攻击！");
            throw new BusinessException(ReturnCodeEnum.ILLEGAL_CHARACTERS_ERROR);
        }
        return src;
    }
}
