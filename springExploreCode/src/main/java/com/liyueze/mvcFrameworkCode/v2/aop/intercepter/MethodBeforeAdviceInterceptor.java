package com.liyueze.mvcFrameworkCode.v2.aop.intercepter;

import com.liyueze.mvcFrameworkCode.v2.aop.invocation.JoinPoint;
import com.liyueze.mvcFrameworkCode.v2.aop.invocation.MethodInvocation;

import java.lang.reflect.Method;


public class MethodBeforeAdviceInterceptor extends AspectAdviceTemplate  {


    private JoinPoint joinPoint;

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }



    private void before() throws Throwable{


    }
    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.joinPoint=mi;
        super.invokeAdviceMethod(this.joinPoint,null,null);
        return mi.proceed();
    }

    @Override
    protected int getSortIndex() {
        return 0;
    }
}
