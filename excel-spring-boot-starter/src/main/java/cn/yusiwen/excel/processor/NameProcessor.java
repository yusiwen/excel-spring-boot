package cn.yusiwen.excel.processor;

import java.lang.reflect.Method;

/**
 * @author yusiwen
 */
public interface NameProcessor {

    /**
     * 解析名称
     *
     * @param args 拦截器对象
     * @param method Method
     * @param key 表达式
     * @return Determined name
     */
    String doDetermineName(Object[] args, Method method, String key);

}
