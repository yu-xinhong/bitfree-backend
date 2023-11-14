package com.jihai.bitfree.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jihai.bitfree.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XssAndSqlHttpServletRequestWrapper.java
 * @author : Immortal Chengge
 * @Description : //TODO
 **/
@Slf4j
public class XssAndSqlHttpServletRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 构造器，将request传递给父类
     * @param request 请求
     */
    public XssAndSqlHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 获取单个参数,将参数进行转义
     * @param name 参数名
     * @return 清洗后参数值
     */
    @Override
    public String getParameter(String name) {
        //获取参数
        String value = super.getParameter(name);
        if(!StrUtil.hasBlank(value)){
            //对参数进行转义
            value = SecurityUtils.cleanXSS(SecurityUtils.cleanSQLInject(value));
        }
        return value;
    }

    /**
     * 获取多个参数进行转义，返回值为数组
     * @param name 参数名
     * @return 清洗后参数值
     */
    @Override
    public String[] getParameterValues(String name) {
        //获取集合
        String[] values = super.getParameterValues(name);
        //判断集合是否为空，如果不为空，进行转义
        if(values != null){
            //遍历数组
            for(int i = 0;i < values.length;i++){
                String value = values[i];
                if(!StrUtil.hasBlank(value)){
                    //转义
                    value = SecurityUtils.cleanXSS(SecurityUtils.cleanSQLInject(value));
                }
                //将转义后的数据放回数组
                values[i] = value;
            }
        }
        return values;
    }

    /**
     * 获取请求头的数据，并进行转义
     * @param name 请求头Key
     * @return 清洗后请求头Value
     */
    @Override
    public String getHeader(String name) {
        String value =  super.getHeader(name);
        if(!StrUtil.hasBlank(value)){
            value = SecurityUtils.cleanXSS(SecurityUtils.cleanSQLInject(value));
        }
        return value;
    }

    /**
     * 获取Map参数
     * @return 清洗后请求Map
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        //因为super.getParameterMap()返回的是Map,所以我们需要定义Map的实现类对数据进行封装
        Map<String,String[]> params = new LinkedHashMap<>();
        //如果参数不为空
        if(parameterMap != null){
            //对map进行遍历
            for(String key:parameterMap.keySet()){
                //根据key获取value
                String[] values = parameterMap.get(key);
                //遍历数组
                for(int i = 0;i<values.length;i++){
                    String value = values[i];
                    if(!StrUtil.hasBlank(value)){
                        //转义
                        value = SecurityUtils.cleanXSS(SecurityUtils.cleanSQLInject(value));
                    }
                    //将转义后的数据放回数组中
                    values[i] = value;
                }
                //将转义后的数组put到linkMap当中
                params.put(key,values);
            }
        }
        return params;
    }

    /**
     * 获取@RequestBody输入流参数（主要针对HTTP POST请求）
     * @return ServletInputStream 请求流
     * @throws IOException 流异常
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        //获取输入流
        ServletInputStream in = super.getInputStream();
        //用于存储输入流
        StringBuilder body = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        //按行读取输入流
        String line = bufferedReader.readLine();
        while(line != null){
            //将获取到的第一行数据append到StringBuffer中
            body.append(line);
            //继续读取下一行流，直到line为空
            line = bufferedReader.readLine();
        }
        //关闭流
        bufferedReader.close();
        reader.close();
        in.close();

        //将body转换为map
        Map<String,Object> map = JSONUtil.parseObj(body.toString());
        //创建空的map用于存储结果
        Map<String,Object> resultMap = new HashMap<>(map.size());
        //遍历数组
        for(String key:map.keySet()){
            Object value = map.get(key);
            //如果map.get(key)获取到的是字符串就需要进行转义，如果不是直接存储resultMap
            if(map.get(key) instanceof String){
                String str = SecurityUtils.cleanXSS(SecurityUtils.cleanSQLInject(value.toString()));
                resultMap.put(key,str);
            }else{
                resultMap.put(key,value);
            }
        }

        //将resultMap转换为json字符串
        String resultStr = JSONUtil.toJsonStr(resultMap);
        //将json字符串转换为字节
        final ByteArrayInputStream bis = new ByteArrayInputStream(resultStr.getBytes());

        //实现接口
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() {
                return bis.read();
            }
        };
    }
}
