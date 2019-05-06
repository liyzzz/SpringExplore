package com.liyueze.demo.v1.controller;

import com.liyueze.demo.v1.service.IUserService;
import com.liyueze.mvcFrameworkCode.v1.annotation.AutowiredV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.ControllerV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.RequestMappingV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.RequestParamV1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerV1
@RequestMappingV1("/user")
public class UserController {
    @AutowiredV1
    private IUserService userService;

    //http://localhost:8080/user/query?name=liyueze&name=lyz
    @RequestMappingV1("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @RequestParamV1("name") String name){
        String result = userService.get(name);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
