package com.liyueze.mvcFrameworkCode.v2.aop;


import com.liyueze.mvcFrameworkCode.v2.aop.intercepter.AspectAdviceTemplate;
import com.liyueze.mvcFrameworkCode.v2.aop.invocation.MethodInvocation;
import com.liyueze.mvcFrameworkCode.v2.aop.support.AdvisedSupport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * cglib代理方式的实现
 * 注意这里的MethodInterceptor是cglib的
 */
public class CglibAopProxy implements AopProxy,MethodInterceptor {

    private AdvisedSupport advised;
    public CglibAopProxy(AdvisedSupport advised) {
        this.advised=advised;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.advised.getTargetClass());//设置父类（既被代理的对象）
        enhancer .setCallback(this);//设置回调，是哪个MethodInterceptor拦截，就会生成该拦截器中的intercept方法执行内容
        enhancer.setClassLoader(classLoader);
        return enhancer.create();//代理对象生成,使用默认无参数的构造函数创建目标对象
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        List<AspectAdviceTemplate> interceptorsAndDynamicMethodMatchers=this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass());
        MethodInvocation invocation = new MethodInvocation(methodProxy,this.advised.getTarget(),method,objects,this.advised.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
