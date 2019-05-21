package com.liyueze.mvcFrameworkCode.v2.beans.config;

import lombok.Data;

/**
 * 存放Bean Class的信息
 */
@Data
public class BeanDefinition {
    //存完整的类名（带包名的）
    private String beanClassName;
    //存工厂中的名字(类名小写)
    private String factoryBeanName;
    private boolean lazyInit = false;
    //把Spring的Bean单例（这个版本只做单例的处理，不考虑原型）
    private boolean singleton = true;

}
