package com.liyueze.mvcFrameworkCode.v2.contex;

import com.liyueze.mvcFrameworkCode.v2.beans.BeanWrapper;
import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanDefinition;
import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanFactoryPostProcessor;
import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanPostProcessor;
import com.liyueze.mvcFrameworkCode.v2.beans.support.BeanDefinitionReader;
import com.liyueze.mvcFrameworkCode.v2.beans.support.DefaultListableBeanFactory;
import com.liyueze.mvcFrameworkCode.v2.core.BeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为简化，不分xml和annotation
 * IOC 和DI操作都在该类中完成
 */
@Slf4j
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {
    private String[] configLocations;
    private BeanDefinitionReader reader;

    //存储存放原始的 bean 对象（尚未填充属性），用于解决循环依赖
    private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);
    //用于存放完全初始化好的 bean，从该缓存中取出的 bean 可以直接使用
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(256);

    /**
     * 依赖注入，从这里开始
     *
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {
        try {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            Object instance = null;
            //Spring初始化bean时对外有两个暴露的扩展点（BeanFactoryPostProcessor和BeanPostProcessor）
            //这里逻辑都简化
            BeanFactoryPostProcessor beanFactoryPostProcessor = (BeanFactoryPostProcessor) getBean(this.beanDefinitionMap.get(BeanFactoryPostProcessor.class.getName()).getFactoryBeanName());
            if (null != beanFactoryPostProcessor) {
                beanFactoryPostProcessor.postProcessBeanFactory(this);
            }
            //初始化
            instance = instantiateBean(beanName, beanDefinition);

            BeanPostProcessor postProcessor = (BeanPostProcessor) getBean(this.beanDefinitionMap.get(BeanPostProcessor.class.getName()).getFactoryBeanName());
            if (null != postProcessor) {
                postProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            //缓存
            this.earlySingletonObjects.put(beanName,instance);
            //把这个对象封装到BeanWrapper中
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            //注入
            populateBean(beanName,new BeanDefinition(),beanWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
    }

    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        return null;
    }


    /**
     * 获取单例Bean
     *
     * @param beanName
     * @param allowEarlyReference 是否提前曝光
     * @return
     */
    public Object getSingleton(String beanName, boolean allowEarlyReference) {
        // 从 singletonObjects 获取实例，singletonObjects 中的实例都是准备好的 bean 实例，可以直接使用
        Object singletonObject = this.singletonObjects.get(beanName);
        if (null == singletonObjects && allowEarlyReference) {
            // 从 earlySingletonObjects 中获取提前曝光的 bean(既只初始化未注入的Bean)
            singletonObject = this.earlySingletonObjects.get(beanName);
        }
        return singletonObject;
    }

    /**
     * 从构造方法开始启动
     *
     * @param configLocations
     */
    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            log.error("启动失败");
            e.printStackTrace();
        }
    }

    /**
     * 开始执行逻辑
     *
     * @throws Exception
     */
    @Override
    public void refresh() throws Exception {
        //定位配置文件
        reader = new BeanDefinitionReader(this.configLocations);
        //加载配置文件，扫描类，并将其封装成BeanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //注册，将BeanDefinition放在applicationContext中（beanDefinitionMap）
        doRegisterBeanDefinition(beanDefinitions);
        //提前初始化lazyInnit=false的类
        doAutowired();
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                log.info("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getBeanName(), beanDefinition);
        }
        //到这里为止，容器初始化完毕
    }

    //只处理非延时加载的情况
    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
