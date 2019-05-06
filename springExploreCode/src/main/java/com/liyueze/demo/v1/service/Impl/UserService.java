package com.liyueze.demo.v1.service.Impl;

import com.liyueze.demo.v1.service.IUserService;
import com.liyueze.mvcFrameworkCode.v1.annotation.ServiceV1;

@ServiceV1
public class UserService implements IUserService {
    public String get(String name) {
        return "My name is " + name;
    }
}
