package com.liyueze.mvcFrameworkCode.v2.contex;

import com.liyueze.mvcFrameworkCode.v2.annotation.AutowiredV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ControllerV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ServiceV2;
import com.liyueze.mvcFrameworkCode.v2.beans.BeanWrapper;
import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanDefinition;
import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanFactoryPostProcessor;
import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanPostProcessor;
import com.liyueze.mvcFrameworkCode.v2.beans.support.BeanDefinitionReader;
import com.liyueze.mvcFrameworkCode.v2.beans.support.DefaultListableBeanFactory;
import com.liyueze.mvcFrameworkCode.v2.core.BeanFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
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
    //真正的IOC容器，包括接口名的Bean也会存着
    private final Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>(16);

    BeanPostProcessor postProcessor=null;
    BeanFactoryPostProcessor beanFactoryPostProcessor=null;

    /**
     * 依赖注入，从这里开始
     *
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {

        BeanWrapper instanceBeanWrapper=this.factoryBeanInstanceCache.get(beanName);
        if(instanceBeanWrapper!=null){
            return instanceBeanWrapper.getWrappedInstance();
        }
        Object instance=null;
        try {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            //Spring初始化bean时对外有两个暴露的扩展点（BeanFactoryPostProcessor和BeanPostProcessor）
            //这里逻辑都简化
            if (null != beanFactoryPostProcessor) {
                beanFactoryPostProcessor.postProcessBeanFactory(this);
            }
            //初始化
            instance = instantiateBean(beanName, beanDefinition);

            if (null != postProcessor) {
                postProcessor.postProcessBeforeInitialization(instance, beanName);
            }
            //然后会执行bean的init方法等，这里省略
            if (null != postProcessor) {
                postProcessor.postProcessAfterInitialization(instance, beanName);
            }

            //把这个对象封装到BeanWrapper中
            BeanWrapper beanWrapper = new BeanWrapper(instance);

            //注入
            populateBean(beanName,new BeanDefinition(),beanWrapper);
            String className=beanDefinition.getBeanClassName();
            this.singletonObjects.put(className,beanWrapper.getWrappedInstance());
            this.earlySingletonObjects.remove(className);
            this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) throws Exception {
        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(ControllerV2.class) || clazz.isAnnotationPresent(ServiceV2.class))){
            return;
        }

        Field[] fields=clazz.getFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(AutowiredV2.class)){ continue;}
            AutowiredV2 autowiredV2=field.getAnnotation(AutowiredV2.class);
            String autowiredBeanName =  autowiredV2.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = toLowerFirstCase(field.getType().getSimpleName());
            }

            field.setAccessible(true);
            String fieldName=field.getType().getName();
            Object autowiredObject=this.singletonObjects.get(fieldName);
            if (autowiredObject==null){
                autowiredObject=this.earlySingletonObjects.get(fieldName);
                if(autowiredObject==null){
                    autowiredObject=getBean(autowiredBeanName);
                }
            }
            if(autowiredObject==null){
                log.info("can not find class autowired:"+fieldName);
                throw new Exception("未找到注入类型:"+fieldName);
            }
            field.set(beanWrapper.getWrappedInstance(),autowiredObject);
        }

    }



    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        //缓存
        try {
            if(this.earlySingletonObjects.containsKey(className)){
                instance = this.singletonObjects.get(className);;
            }else{
                Class clazz=Class.forName(className);
                instance=clazz.newInstance();
                this.earlySingletonObjects.put(className,instance);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return instance;
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
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //到这里为止，容器初始化完毕
    }

    //只处理非延时加载的情况
    private void doAutowired() {
        //先把需要用的两个扩展点装载好
        BeanDefinition bfpp=this.beanDefinitionMap.get(toLowerFirstCase(BeanFactoryPostProcessor.class.getSimpleName()));
        BeanDefinition bpp=this.beanDefinitionMap.get(toLowerFirstCase(BeanPostProcessor.class.getSimpleName()));
        if(bfpp!=null){
            beanFactoryPostProcessor =(BeanFactoryPostProcessor)getBean(bfpp.getBeanClassName());
        }
        if(bpp!=null){
            postProcessor=(BeanPostProcessor) getBean(bpp.getBeanClassName());
        }
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
