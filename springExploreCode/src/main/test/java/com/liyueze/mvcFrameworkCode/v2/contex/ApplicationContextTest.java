package com.liyueze.mvcFrameworkCode.v2.contex;


import com.liyueze.demo.v2.action.MyAction;
import com.liyueze.demo.v2.service.IModifyService;
import com.liyueze.demo.v2.service.IQueryService;
import com.liyueze.demo.v2.service.impl.DeleteService;
import org.junit.Test;

public class ApplicationContextTest {
    @Test
    public void test01(){
        ApplicationContext application=new ApplicationContext("classpath:applicationV2.properties");
        IQueryService iQueryService= (IQueryService) application.getBean("queryService");
        iQueryService.queryAll();
        IModifyService iModifyService=(IModifyService)application.getBean("iModifyService");
        iModifyService.add("liyzzz","GS");
        MyAction myAction= (MyAction) application.getBean("myAction");
        myAction.test(1,"14");
    }

    @Test
    public void test02(){
        ApplicationContext application=new ApplicationContext("classpath:applicationV2.properties");
        IQueryService iQueryService= (IQueryService) application.getBean("queryService");
        iQueryService.queryError();
    }

    @Test
    public void test03(){
        ApplicationContext application=new ApplicationContext("classpath:applicationV2.properties");
        DeleteService deleteService= (DeleteService) application.getBean("deleteService");
        deleteService.delete();
    }

}