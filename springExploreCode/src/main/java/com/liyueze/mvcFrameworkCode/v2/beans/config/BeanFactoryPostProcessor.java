package com.liyueze.mvcFrameworkCode.v2.beans.config;

import com.liyueze.mvcFrameworkCode.v2.contex.ApplicationContext;

/**
 * 实现该接口，可以在spring的bean创建之前，修改bean的定义属性。
 * 也就是说，Spring允许BeanFactoryPostProcessor在容器实例化任何其它bean之前读取配置元数据，
 * 并可以根据需要进行修改
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ApplicationContext beanFactory) throws Exception;
}
