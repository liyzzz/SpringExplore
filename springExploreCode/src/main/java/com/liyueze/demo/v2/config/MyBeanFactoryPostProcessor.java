package com.liyueze.demo.v2.config;

import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanFactoryPostProcessor;
import com.liyueze.mvcFrameworkCode.v2.contex.ApplicationContext;

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ApplicationContext beanFactory) throws Exception {
        System.out.println("这是BeanFactoryPostProcessor");
    }
}
