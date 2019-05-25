package com.liyueze.mvcFrameworkCode.v2.springmvn.servlet;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Model {
    private Map<String,Object> model=new HashMap<>();

    public void put(String key,Object value){
        this.model.put(key,value);
    }
}
