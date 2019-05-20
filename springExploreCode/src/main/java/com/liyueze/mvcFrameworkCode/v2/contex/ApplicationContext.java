package com.liyueze.mvcFrameworkCode.v2.contex;

import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanDefinition;
import com.liyueze.mvcFrameworkCode.v2.beans.support.BeanDefinitionReader;
import com.liyueze.mvcFrameworkCode.v2.beans.support.DefaultListableBeanFactory;
import com.liyueze.mvcFrameworkCode.v2.core.BeanFactory;

import java.util.List;

/**
 * 为简化，不分xml和annotation
 * IOC 和DI操作都在该类中完成
 */

public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {
    private String[] configLactions;
    private BeanDefinitionReader reader;

    @Override
    public Object getBean(String beanName) {
        return null;
    }

    /**
     * 从构造方法开始启动
     * @param configLactions
     */
    public ApplicationContext(String ...configLactions){
        this.configLactions=configLactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始执行逻辑
     * @throws Exception
     */
    @Override
    public void refresh() throws Exception {
        //定位配置文件
        reader=new BeanDefinitionReader(this.configLactions);
        //加载配置文件，扫描类，并将其封装成BeanDefinition
        List<BeanDefinition> beanDefinitions=reader.loadBeanDefinitions();
        //注册，将BeanDefinition放在applicationContext中（beanDefinitionMap）
        doRegisterBeanDefinition(beanDefinitions);
        //提前初始化lazyInnit=false的类
        doAutowrited();
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) {
    }

    private void doAutowrited() {
    }


}
