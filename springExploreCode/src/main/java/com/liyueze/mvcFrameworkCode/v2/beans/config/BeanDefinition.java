package com.liyueze.mvcFrameworkCode.v2.beans.config;

import lombok.Data;

@Data
public class BeanDefinition {
    private String beanName;
    private boolean lazyInit = false;
    private String factoryBeanName;
    private  boolean isSingleton=true;


}
