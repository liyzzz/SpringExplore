package com.liyueze.mvcFrameworkCode.v2.beans;

public interface BeanFactory {
    /**
     * 根据BeanName从IOC容器中获取一个实例Bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);
}
