package com.liyueze.demo.v2.config;

import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        System.out.println("postProcessBeforeInitialization:"+beanName);
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        System.out.println("postProcessAfterInitialization:"+beanName);
        return null;
    }
}
