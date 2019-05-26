package com.liyueze.mvcFrameworkCode.v2.aop;

/**
 * 定义AOP顶层的接口
 * 获得代理对象
 */
public interface AopProxy {

    Object getProxy();


    Object getProxy(ClassLoader classLoader);
}
