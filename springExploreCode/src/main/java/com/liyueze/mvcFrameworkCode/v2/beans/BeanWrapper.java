package com.liyueze.mvcFrameworkCode.v2.beans;

import lombok.Data;

@Data
public class BeanWrapper {
    //Bean实例
    private Object wrappedInstance;
    //Bean class
    private Class<?> wrappedClass;


    public BeanWrapper(Object instance, String beanClassName) {
        this.wrappedInstance = instance;
        try {
            this.wrappedClass=Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }




}
