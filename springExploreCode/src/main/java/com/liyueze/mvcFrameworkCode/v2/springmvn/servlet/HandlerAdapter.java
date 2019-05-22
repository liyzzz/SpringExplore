package com.liyueze.mvcFrameworkCode.v2.springmvn.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerAdapter {
    //那种adapter只支持那种Handler
    public boolean supports(Object handler){ return (handler instanceof HandlerMapping);}

    //具体执行HandleMapping中保存的方法
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        return null;
    }

}
