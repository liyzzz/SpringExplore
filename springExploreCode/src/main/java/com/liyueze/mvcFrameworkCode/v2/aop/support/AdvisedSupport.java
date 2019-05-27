package com.liyueze.mvcFrameworkCode.v2.aop.support;

import com.liyueze.mvcFrameworkCode.v2.aop.config.AopConfig;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 将代理需要的基本参数保存下来并解析
 * 生成拦截器
 */

@Data
public class AdvisedSupport {
    //存AOP的配置信息
    private AopConfig aopConfig;
    //被代理的类
    private Class<?> targetClass;
    //被代理的对象
    private Object target;

    public AdvisedSupport(AopConfig aopConfig) {
        this.aopConfig = aopConfig;
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
