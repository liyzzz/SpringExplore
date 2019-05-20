package com.liyueze.mvcFrameworkCode.v2.beans.config;

import lombok.Data;

/**
 * 存放Bean Class的信息
 */
@Data
public class BeanDefinition {
    //存完成的类名（带包名的）
    private String beanName;
    //存工厂中的名字,可重复，表明来源
    private String factoryBeanName;
    private boolean lazyInit = false;
    private boolean singleton = true;
    //是否是beanFactoryPostProcessor
    private boolean beanFactoryPostProcessor=false;
    //是否是beanPostProcessor
    private boolean beanPostProcessor=false;

}
