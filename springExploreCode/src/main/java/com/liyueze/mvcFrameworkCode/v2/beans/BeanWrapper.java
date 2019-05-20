package com.liyueze.mvcFrameworkCode.v2.beans;

/**
 * Created by Tom.
 */
public class BeanWrapper {
    //Bean实例
    private Object wrappedInstance;
    //Bean class
    private Class<?> wrappedClass;

    public BeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }


}
