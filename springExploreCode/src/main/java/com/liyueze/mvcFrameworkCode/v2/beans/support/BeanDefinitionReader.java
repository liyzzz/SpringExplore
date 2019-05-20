package com.liyueze.mvcFrameworkCode.v2.beans.support;


import com.liyueze.mvcFrameworkCode.v2.beans.config.BeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 该类是用来读取配置文件，保存配置文件的一些信息
 */
@Slf4j
public class BeanDefinitionReader {
    private List<Properties> configs=new ArrayList<>();

    /**
     * 该构造方法将String配置文件的位置转化为properties并保存在该类中
     * @param configLactions
     * @throws IOException
     */
    public BeanDefinitionReader(String[] configLactions) throws IOException {
        InputStream inputStream;
        Properties config;
        for(String configLaction:configLactions){
            if(!configLaction.matches("classpath[\\*]?:.*")){
                continue;
            }
            inputStream=this.getClass().getClassLoader().getResourceAsStream(configLaction.replaceFirst("classpath[\\*]?:",""));
            config=new Properties();
            config.load(inputStream);
            configs.add(config);
        }
    }

    /**
     * 读取配置信息信息中需要扫描的类
     * @return
     */
    public List<BeanDefinition> loadBeanDefinitions() {
        return  null;
    }
}
