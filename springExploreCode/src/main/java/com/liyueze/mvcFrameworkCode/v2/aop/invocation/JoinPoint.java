package com.liyueze.mvcFrameworkCode.v2.aop.invocation;

import java.lang.reflect.Method;

/**
 * MethodInvocation方法调用器就是一个JoinPoint
 */
public interface JoinPoint {
    /**
     * @return 被代理对象
     */
    Object getThis();

    /**
     * @return 被代理对象方法参数
     */
    Object[] getArguments();

    /**
     *
     * @return 被代理对象方法
     */
    Method getMethod();

    /**
     * 设置用户自定义保存的变量
     * @param key
     * @param value
     */
    void setUserAttribute(String key, Object value);

    /**
     *
     * @param key
     * @return 用户自定义保存的变量
     */
    Object getUserAttribute(String key);
}
