package com.liyueze.mvcFrameworkCode.v2.aop.intercept;

/**
 * 方法拦截器标准
 * 组成方法拦截器链
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
