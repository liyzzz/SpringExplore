package com.liyueze.mvcFrameworkCode.v2.contex;


import org.junit.Test;

public class ApplicationContextTest {
    @Test
    public void test01(){
        ApplicationContext application=new ApplicationContext("classpath:applicationV2.properties");
        application.getBean("myAction");
    }

}