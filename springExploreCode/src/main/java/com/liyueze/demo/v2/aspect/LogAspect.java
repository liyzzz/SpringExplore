package com.liyueze.demo.v2.aspect;

import com.liyueze.mvcFrameworkCode.v2.aop.invocation.JoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 该类中的所有方法不能重载
 */

@Slf4j
public class LogAspect {

    //在调用一个方法之前，执行before方法
    public void before(JoinPoint joinPoint){
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        //这个方法中的逻辑，是由我们自己写的
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nTargetMethod:" +  joinPoint.getMethod() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    //在调用一个方法之后，执行after方法
    public void after(JoinPoint joinPoint){
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nTargetMethod:" +  joinPoint.getMethod() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    public void afterThrowing(JoinPoint joinPoint, Throwable ex){
        log.info("出现异常" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nTargetMethod:" +  joinPoint.getMethod() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }

}
