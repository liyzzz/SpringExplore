package com.liyueze.mvcFrameworkCode.v2.aop.intercept;


import java.lang.reflect.Method;
import java.util.List;

/**
 * 相当于Spring中的ReflectiveMethodInvocation
 * 方法调用器：执行拦截器链
 */
public class MethodInvocation  {
    //生成的代理对象
    private Object proxy;
    //代理方法
    private Method method;
    //被代理的对象
    private Object target;
    //参数
    private Object [] arguments;
    //拦截器链
    private List<Object> interceptorsAndDynamicMethodMatchers;
    //被代理的类
    private Class<?> targetClass;


    //保存代理中需要参数
    public MethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {

        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }
    //执行下一个拦截器链
    public Object proceed() throws Throwable {
        return null;
    }
}
