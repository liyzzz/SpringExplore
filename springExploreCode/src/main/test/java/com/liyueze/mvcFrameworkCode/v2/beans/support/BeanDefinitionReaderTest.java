package com.liyueze.mvcFrameworkCode.v2.beans.support;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class BeanDefinitionReaderTest {
    BeanDefinitionReader beanDefinitionReader;
    @Before
    public void constructTest(){
        try {
            beanDefinitionReader=new BeanDefinitionReader(new String[]{"classpath:applicationV2.properties","classpath:application.properties"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadBeanDefinitionsTest(){
        try {
            beanDefinitionReader.loadBeanDefinitions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}