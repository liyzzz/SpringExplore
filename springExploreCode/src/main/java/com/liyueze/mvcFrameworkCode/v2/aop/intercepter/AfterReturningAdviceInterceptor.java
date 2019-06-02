package com.liyueze.mvcFrameworkCode.v2.aop.intercepter;

import com.liyueze.mvcFrameworkCode.v2.aop.invocation.JoinPoint;
import com.liyueze.mvcFrameworkCode.v2.aop.invocation.MethodInvocation;

import java.lang.reflect.Method;


public class AfterReturningAdviceInterceptor extends AspectAdviceTemplate  {

    private JoinPoint joinPoint;

    public AfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }



    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinPoint=mi;
        Object retVal=mi.proceed();
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
        return retVal;
    }


    @Override
    protected int getSortIndex() {
        return 1;
    }
}
