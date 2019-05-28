package com.liyueze.mvcFrameworkCode.v2.aop.invocation;


import com.liyueze.mvcFrameworkCode.v2.aop.intercepter.AspectAdviceTemplate;
import com.liyueze.mvcFrameworkCode.v2.aop.intercepter.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 相当于Spring中的ReflectiveMethodInvocation
 * 方法调用器：执行拦截器链
 */
public class MethodInvocation implements JoinPoint {
    //生成的代理对象
    private Object proxy;
    //被代理方法
    private Method method;
    //被代理的对象
    private Object target;
    //被代理方法的参数
    private Object[] arguments;
    //拦截器链
    private List<AspectAdviceTemplate> interceptorsAndDynamicMethodMatchers;
    //被代理的类
    private Class<?> targetClass;

    //用户自定义保存的变量
    private Map<String, Object> userAttributes;

    //定义一个索引，从-1开始来记录当前拦截链执行的位置
    private int currentInterceptorIndex = -1;

    //保存代理中需要参数
    public MethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<AspectAdviceTemplate> interceptorsAndDynamicMethodMatchers) {

        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    //执行拦截器(递归)
    public Object proceed() throws Throwable {
        //如果执行链执行完了（需要被代理方法执行后才能执行的方法（例如after）会被压栈）或执行链为空就调用被代理方法
        if(null==interceptorsAndDynamicMethodMatchers
                || interceptorsAndDynamicMethodMatchers.size()-1-currentInterceptorIndex<=0){
            return this.method.invoke(this.target,this.arguments);
        }
        currentInterceptorIndex++;
        //获取下一个执行器
        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(currentInterceptorIndex);
        if(interceptorOrInterceptionAdvice instanceof MethodInterceptor){
            return ((MethodInterceptor)interceptorOrInterceptionAdvice).invoke(this);
        }else{
            //匹配失败，略过当前拦截器，执行下一个
            return proceed();
        }
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (null == userAttributes) {
            userAttributes = new HashMap<>();
        }
        this.userAttributes.put(key, value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
