package com.liyueze.mvcFrameworkCode.v2.beans.support;


import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 该类是用来读取配置文件，保存配置文件的一些信息
 */
@Slf4j
public class BeanDefinitionReader {

    private List<Properties> configs = new ArrayList<>();
    List<BeanDefinition> beanDefinitions = new ArrayList<>();

    //固定配置文件中的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    /**
     * 该构造方法将String配置文件的位置转化为properties并保存在该类中
     *
     * @param configLocations
     * @throws IOException
     */
    public BeanDefinitionReader(String[] configLocations) throws IOException {
        InputStream inputStream;
        Properties config;
        for (String configLocation : configLocations) {
            if (!configLocation.matches("classpath[\\*]?:.*")) {
                log.info("未匹配到该路径");
                continue;
            }
            inputStream = this.getClass().getClassLoader().getResourceAsStream(configLocation.replaceFirst("classpath[\\*]?:", ""));
            config = new Properties();
            config.load(inputStream);
            configs.add(config);
        }
    }

    /**
     * 读取配置信息信息中需要扫描的类
     *
     * @return
     */
    public List<BeanDefinition> loadBeanDefinitions() throws Exception {
        for (Properties config : configs) {
            doScanner(config.getProperty(SCAN_PACKAGE));
        }
        return beanDefinitions;
    }


    private BeanDefinition createBeanDefinition(String beanName, String factoryName) {
        BeanDefinition beanDefinition=new BeanDefinition();
        beanDefinition.setBeanClassName(beanName);
        beanDefinition.setFactoryBeanName(factoryName);
        return beanDefinition;
    }

    private void doScanner(String scanPackage) throws Exception {
        //转换为文件路径，实际上就是把.替换为/就OK了
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else{
                if(!file.getName().endsWith(".class")){ continue;}
                String className = (scanPackage + "." + file.getName().replace(".class",""));
                doCreateBeanDefinition(className);
            }
        }
    }

    private void doCreateBeanDefinition(String className) throws Exception {
        try {
            Class clazz=Class.forName(className);
            if(clazz.isInterface()){
                return;
            }
            String beanFactoryName=toLowerFistWord(clazz.getSimpleName());
            beanDefinitions.add(createBeanDefinition(className,beanFactoryName));
            //将接口信息也保存下来
            Class[] interfaces=clazz.getInterfaces();
            for(Class i:interfaces){
                //如果是多个实现类，只能覆盖(Spring也是如此，这个时候可以自定义名)
                BeanDefinition beanDefinition=createBeanDefinition(className,toLowerFistWord(i.getSimpleName()));
                beanDefinitions.add(beanDefinition);
            }
        } catch (ClassNotFoundException e) {
            log.info("未找见类:"+className);
            throw new Exception("未找见类");
        }

    }

    private String toLowerFistWord(String simpleName) {
        return simpleName.substring(0,1).toLowerCase()+simpleName.substring(1);
    }

    public List<Properties> getConfig(){
        return configs;
    }
}
