package com.liyueze.mvcFrameworkCode.v2.aop.intercepter;

import com.liyueze.mvcFrameworkCode.v2.aop.invocation.JoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 把公用的方法抽象到该方法中
 */
@Slf4j
public abstract class AspectAdviceTemplate implements Comparable<AspectAdviceTemplate>,MethodInterceptor {
    //切面
    private Method aspectMethod;
    //被执行的方法
    private Object aspectTarget;
    public AspectAdviceTemplate(Method aspectMethod, Object aspectTarget) {
       this.aspectMethod = aspectMethod;
       this.aspectTarget = aspectTarget;
    }

    /**
     * 执行某个切面中的某个方法
     * @param joinPoint
     * @param returnValue 返回值
     * @param tx 异常
     * @return
     * @throws Throwable
     */
    public Object invokeAdviceMethod(JoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable{
        Class<?> [] paramTypes = this.aspectMethod.getParameterTypes();
        if(null == paramTypes || paramTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }else{
            //切面中的方法只能有三种类型：1.JoinPoint 2.异常（Throwable:error和Exception）3.返回值
            Object [] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i ++) {
                if(paramTypes[i] == JoinPoint.class){
                    args[i] = joinPoint;
                }else if(paramTypes[i] == Throwable.class){
                    args[i] = tx;
                }else if(paramTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget,args);
        }
    }

    @Override
    public int compareTo(AspectAdviceTemplate aspectAdviceTemplate) {
        return this.getSortIndex()-aspectAdviceTemplate.getSortIndex();
    }

    protected abstract int getSortIndex();
}
