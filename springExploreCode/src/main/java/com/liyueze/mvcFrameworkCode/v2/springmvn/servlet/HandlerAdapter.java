package com.liyueze.mvcFrameworkCode.v2.springmvn.servlet;

import com.liyueze.mvcFrameworkCode.v2.annotation.RequestParamV2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class HandlerAdapter {
    //那种adapter只支持那种Handler
    public boolean supports(Object handler){ return (handler instanceof HandlerMapping);}

    //具体执行HandleMapping中保存的方法
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMapping handler) throws Exception{
        Object controller=handler.getController();
        Method method=handler.getMethod();
        Annotation[][] parametersAnnotations=method.getParameterAnnotations();
        for(Annotation[] annotations:parametersAnnotations){
            for(Annotation annotation:annotations){
                if(RequestParamV2.class==annotation.annotationType()){
                    String paramName=((RequestParamV2)annotation).value();
                    String  paramValue=request.getParameter(paramName);
                    method.getParameterTypes();
                }
            }
        }
        return null;
    }

}
