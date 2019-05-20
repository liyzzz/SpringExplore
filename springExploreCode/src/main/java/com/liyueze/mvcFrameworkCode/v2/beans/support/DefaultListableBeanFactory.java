package com.liyueze.mvcFrameworkCode.v2.beans.support;

import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanDefinition;
import com.liyueze.mvcFrameworkCode.v2.contex.support.AbstractApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class DefaultListableBeanFactory extends AbstractApplicationContext {

    //存储注册信息的BeanDefinition
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();


}
