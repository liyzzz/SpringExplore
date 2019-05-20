package com.liyueze.mvcFrameworkCode.v2.beans.config;

/**
 * 对Bean进行加工，
 * 如果我们需要在Spring容器完成Bean的实例化、配置和其他的初始化前后添加一些自己的逻辑处理，
 * 我们就可以定义一个或者多个BeanPostProcessor接口的实现，然后注册到容器中。
 */
public interface BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception ;

    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception ;
}
