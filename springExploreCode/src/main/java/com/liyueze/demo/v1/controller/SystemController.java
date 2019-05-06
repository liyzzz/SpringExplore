package com.liyueze.demo.v1.controller;

import com.liyueze.mvcFrameworkCode.v1.annotation.ControllerV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.RequestMappingV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.RequestParamV1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerV1
@RequestMappingV1("/system")
public class SystemController {

    //http://localhost:8080/system/add?a=1&b=3
    @RequestMappingV1("/add")
    public void add(HttpServletRequest req, HttpServletResponse resp,
                    @RequestParamV1("a") Integer a, @RequestParamV1("b") Integer b){
        try {
            resp.getWriter().write(a + "+" + b + "=" + (a + b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMappingV1("/sub")
    public void add(HttpServletRequest req, HttpServletResponse resp,
                    @RequestParamV1("a") Double a, @RequestParamV1("b") Double b){
        try {
            resp.getWriter().write(a + "-" + b + "=" + (a - b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMappingV1("/remove")
    public String  remove(@RequestParamV1("id") Integer id){
        return "" + id;
    }
}
