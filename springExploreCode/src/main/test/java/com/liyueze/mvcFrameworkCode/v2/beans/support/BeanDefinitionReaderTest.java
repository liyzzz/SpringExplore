package com.liyueze.mvcFrameworkCode.v2.beans.support;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;


public class BeanDefinitionReaderTest {

    @Test
    public void constructTest(){
        try {
            BeanDefinitionReader beanDefinitionReader=new BeanDefinitionReader(new String[]{"classpath:applicationV2.properties","classpath:application.properties"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}