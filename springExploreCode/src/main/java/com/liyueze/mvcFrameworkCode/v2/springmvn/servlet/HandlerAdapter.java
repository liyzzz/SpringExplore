package com.liyueze.mvcFrameworkCode.v2.springmvn.servlet;

import com.liyueze.mvcFrameworkCode.v2.annotation.RequestParamV2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public class HandlerAdapter {
    //那种adapter只支持那种Handler
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMapping);
    }

    //具体执行HandleMapping中保存的方法
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, HandlerMapping handler) throws Exception {
        Object controller = handler.getController();
        Method method = handler.getMethod();
        Annotation[][] annotations = method.getParameterAnnotations();
        Class[] clazzList = method.getParameterTypes();
        //保存执行时的参数
        Object[] parameter = new Object[clazzList.length];
        //存这个参数有没有被注解
        Boolean[] isAnnotation = new Boolean[clazzList.length];
        for (int i = 0; i < clazzList.length; i++) {
            //默认值都设置为false
            isAnnotation[i]=false;
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof RequestParamV2) {
                    String parameterName = ((RequestParamV2) annotation).value();
                    if (parameterName == null) {
                        throw new Exception("must have RequestParamV2 value");
                    }
                    String parameterValue = request.getParameter(parameterName);
                    Object parameterCase = CaseStringValue(parameterValue, clazzList[i]);
                    parameter[i] = parameterCase;
                    isAnnotation[i] = true;
                }
            }
        }
        Model model=null;
        for (int i = 0; i < isAnnotation.length; i++) {
            if (isAnnotation[i]) {
                continue;
            }
            Class clazz = clazzList[i];
            if (clazz == HttpServletRequest.class) {
                parameter[i] = request;
            } else if (clazz == HttpServletResponse.class) {
                parameter[i] = response;
            } else if (clazz == HttpSession.class) {
                parameter[i] = request.getSession();
                //其他的省略
            } else if (clazz == Model.class) {
                model=new Model();
                parameter[i] =model;
            } else {
                throw new Exception("don't found this parameter:" + clazzList[i]);
            }
        }
        Object result = method.invoke(controller, parameter);
        if (result instanceof ModelAndView) {
            ModelAndView modelAndView=(ModelAndView) result;
            modelAndView.setModel(model);
            return modelAndView;
        }
        return null;
    }

    private Object CaseStringValue(String parameterValue, Class clazz) {
        if (String.class == clazz) {
            return parameterValue;
        }
        //如果是int
        if (Integer.class == clazz) {
            return Integer.valueOf(parameterValue);
        } else if (Double.class == clazz) {
            return Double.valueOf(parameterValue);
        } else {
            // ......省略其他类型
            return null;
        }
    }

}
