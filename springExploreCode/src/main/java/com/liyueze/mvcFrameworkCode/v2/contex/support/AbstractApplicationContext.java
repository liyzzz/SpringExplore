package com.liyueze.mvcFrameworkCode.v2.contex.support;

/**
 * IOC容器实现的顶层设计
 */
public abstract class AbstractApplicationContext {
    //提供refesh方法
    public abstract void refresh() throws Exception ;
}
