package com.liyueze.mvcFrameworkCode.v2.aop;


import com.liyueze.mvcFrameworkCode.v2.aop.AopProxy;
import com.liyueze.mvcFrameworkCode.v2.aop.support.AdvisedSupport;

/**
 * cglib代理方式的实现
 */
public class CglibAopProxy implements AopProxy {
    private AdvisedSupport advisedSupport;
    public CglibAopProxy(AdvisedSupport config) {
        this.advisedSupport=config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
