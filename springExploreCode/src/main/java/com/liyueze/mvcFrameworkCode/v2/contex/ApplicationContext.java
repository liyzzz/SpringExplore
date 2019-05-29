package com.liyueze.mvcFrameworkCode.v2.contex;

import com.liyueze.mvcFrameworkCode.v2.annotation.AutowiredV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ControllerV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.ServiceV2;
import com.liyueze.mvcFrameworkCode.v2.aop.AopProxy;
import com.liyueze.mvcFrameworkCode.v2.aop.CglibAopProxy;
import com.liyueze.mvcFrameworkCode.v2.aop.JdkDynamicAopProxy;
import com.liyueze.mvcFrameworkCode.v2.aop.config.AopConfig;
import com.liyueze.mvcFrameworkCode.v2.aop.support.AdvisedSupport;
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
import java.util.Properties;
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
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    //用于存放完全初始化好的 bean，从该缓存中取出的 bean 可以直接使用
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    //真正的IOC容器，包括接口名的Bean也会存着
    private final Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>(16);

    private BeanPostProcessor postProcessor = null;
    private BeanFactoryPostProcessor beanFactoryPostProcessor = null;

    /**
     * 依赖注入，从这里开始
     *
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {

        BeanWrapper instanceBeanWrapper = this.factoryBeanInstanceCache.get(beanName);
        if (instanceBeanWrapper != null) {
            return instanceBeanWrapper.getWrappedInstance();
        }
        Object instance = null;
        try {
            BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
            //Spring初始化bean时对外有两个暴露的扩展点（BeanFactoryPostProcessor和BeanPostProcessor）
            //这里逻辑都简化
            if (beanDefinition == null) {
                throw new Exception("未找到beanDefinition:" + beanName);
            }
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
            BeanWrapper beanWrapper = new BeanWrapper(instance,beanDefinition.getBeanClassName());

            //注入
            populateBean(beanName, new BeanDefinition(), beanWrapper);
            this.singletonObjects.put(beanName, beanWrapper.getWrappedInstance());
            this.earlySingletonObjects.remove(beanName);
            this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    @Override
    public Object getBean(Class beanClazz) {
        return getBean(toLowerFirstCase(beanClazz.getSimpleName()));
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) throws Exception {
        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if (!(clazz.isAnnotationPresent(ControllerV2.class) || clazz.isAnnotationPresent(ServiceV2.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(AutowiredV2.class)) {
                continue;
            }
            AutowiredV2 autowiredV2 = field.getAnnotation(AutowiredV2.class);
            String autowiredBeanName = autowiredV2.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = toLowerFirstCase(field.getType().getSimpleName());
            }

            field.setAccessible(true);
            //先尝试去取代理的注入对象
            Object autowiredObject = getSingletonObject(autowiredBeanName+"_proxy");
            if(autowiredObject==null){
                //如果代理的注入对象没有则取正常的对象
                autowiredObject=getSingletonObject(autowiredBeanName);
            }
            if (autowiredObject == null) {
                autowiredObject = getBean(autowiredBeanName);
            }
            if (autowiredObject == null) {
                log.info("can not find class autowired:" + autowiredBeanName);
                throw new Exception("未找到注入类型:" + autowiredBeanName);
            }
            field.set(getSingletonObject(beanName), autowiredObject);
        }

    }

    public Object getSingletonObject(String name) {
        Object autowiredObject = this.singletonObjects.get(name);
        if (autowiredObject == null) {
            autowiredObject = this.earlySingletonObjects.get(name);
        }
        return autowiredObject;
    }

    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        Object instance = null;
        //缓存
        try {
            String beanClassName = beanDefinition.getBeanClassName();
            String name = toLowerFirstCase(beanClassName.substring(beanClassName.lastIndexOf('.') + 1));
            //看接口和实现类那个先创建，那个先创建就取那个
            instance = getSingletonObject(name);
            Class clazz = Class.forName(beanDefinition.getBeanClassName());
            if (instance == null) {
                instance = clazz.newInstance();
                this.earlySingletonObjects.put(beanName, instance);
                instance = getProxyObject(beanName, instance, clazz);
            }else if(!beanClassName.equals(name)){
                this.earlySingletonObjects.put(beanName, instance);
                instance = getProxyObject(beanName, instance, clazz);
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

    private Object getProxyObject(String beanName, Object instance, Class clazz) {
        //获取到AdvisedSupport
        AopConfig aopConfig = instanceAopConfig();
        String aspectClass = aopConfig.getAspectClass();
        if(aspectClass==null){
            return instance;
        }
        String aspectName = toLowerFirstCase(aspectClass.substring(aspectClass.lastIndexOf('.') + 1));
        //如果beanName就是切面名，不可以创建代理(如果可创建就会变成没有出口的递归)
        if(aspectName.equals(beanName)){
           return instance;
        }
        AdvisedSupport advised= new AdvisedSupport(aopConfig, getBean(aspectName));
        advised.setTargetClass(clazz);
        advised.setTarget(instance);
        //符合PointCut的规则,就用代理对象代替原来的
        if (advised.pointCutMatch()) {
            instance = createProxy(advised).getProxy();
            this.earlySingletonObjects.put(beanName+"_proxy", instance);
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
        beanFactoryPostProcessor = (BeanFactoryPostProcessor) getBean(BeanFactoryPostProcessor.class);
        postProcessor = (BeanPostProcessor) getBean(BeanPostProcessor.class);
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

    //返回所有的BeanDefinition的Name
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public List<Properties> getConfig() {
        return reader.getConfig();
    }

    /**
     * 根据BeanDefinition去创建advisedSupport
     *
     * @return
     */
    private AopConfig instanceAopConfig() {
        AopConfig aopConfig = new AopConfig();
        List<Properties> configs = this.reader.getConfig();
        for (Properties config : configs) {
            String pointCut = config.getProperty("pointCut");
            //如果这个配置文件中有切点则这个文件中有AOP的所有配置（自己定义的规则，spring中不是这样的）
            if (pointCut != null && !"".equals(pointCut.trim())) {
                aopConfig.setPointCut(pointCut);
                aopConfig.setAspectClass(config.getProperty("aspectClass"));
                aopConfig.setAspectBefore(config.getProperty("aspectBefore"));
                aopConfig.setAspectAfter(config.getProperty("aspectAfter"));
                aopConfig.setAspectAfterThrow(config.getProperty("aspectAfterThrow"));
                aopConfig.setAspectAfterThrowingName(config.getProperty("aspectAfterThrowingName"));
            }
        }
        return aopConfig;
    }

    /**
     * 根据advised去选择创建时JDK代理还是cglib代理
     *
     * @param advised
     * @return
     */
    private AopProxy createProxy(AdvisedSupport advised) {
        if (advised.getTargetClass() != null && advised.getTargetClass().getInterfaces() != null
                && advised.getTargetClass().getInterfaces().length > 0) {
            return new JdkDynamicAopProxy(advised);
        }
        return new CglibAopProxy(advised);
    }
}
