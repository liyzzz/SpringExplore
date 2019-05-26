package com.liyueze.mvcFrameworkCode.v2.aop;



import com.liyueze.mvcFrameworkCode.v2.aop.AopProxy;
import com.liyueze.mvcFrameworkCode.v2.aop.intercept.MethodInvocation;
import com.liyueze.mvcFrameworkCode.v2.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * jdk代理方式的实现
 */
public class JdkDynamicAopProxy implements AopProxy,InvocationHandler{

    private AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport config){
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        //返回代理对象
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    /**
     *
     * @param proxy 生成的代理对象
     * @param method 需要代理的方法
     * @param args 参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //获得被代理方法的执行链
        List<Object> interceptorsAndDynamicMethodMatchers=this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        MethodInvocation invocation = new MethodInvocation(proxy,null,method,args,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
