package com.liyueze.mvcFrameworkCode.v2.aop.support;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 用来完成解析config中代理信息
 * 将代理需要的基本参数保存下来
 */

@Data
public class AdvisedSupport {
    //被代理的类
    private Class<?> targetClass;
    //被代理的对象
    private Object target;

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return this.target;
    }

    /**
     *
     * @param method
     * @param targetClass
     * @return 返回拦截器链
     * @throws Exception
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{


        return null;
    }

    /**
     * 判断该对象是否符合pointCut的规则
     * @return
     */
    public boolean pointCutMatch() {
        return  false;
    }
}
