package com.liyueze.mvcFrameworkCode.v2.aop.intercepter;

import com.liyueze.mvcFrameworkCode.v2.aop.invocation.MethodInvocation;

import java.lang.reflect.Method;


public class AfterThrowingAdviceInterceptor extends AspectAdviceTemplate  {



    public AfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }



    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            return null;
        }
    }


    @Override
    protected int getSortIndex() {
        return 2;
    }
}
