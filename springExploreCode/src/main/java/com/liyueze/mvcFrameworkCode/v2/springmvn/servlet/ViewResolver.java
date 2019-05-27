package com.liyueze.mvcFrameworkCode.v2.springmvn.servlet;

import lombok.Data;

import java.io.File;
import java.net.URL;
import java.util.Locale;

/**
 * 保存配置文件中配置的视图的根文件夹和视图后缀
 * 根据模板适配不同的view
 */
@Data
public class ViewResolver {
    //后缀(这里直接写死)只做一种文件的解析
    private static final String DEFAULT_TEMPLATE_SUFFX=".html";
    //根文件
    private File templateRootDir;

    public ViewResolver(String templateRoot) {
        URL url=this.getClass().getClassLoader().getResource(templateRoot);
        if(null!=url){
            this.templateRootDir = new File(url.getFile());
        }
    }

    public boolean support(String viewName){
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName+DEFAULT_TEMPLATE_SUFFX).replaceAll("/+","/"));
        return templateFile.exists();
    }

    public View resolveViewName(String viewName, Locale locale){
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName+DEFAULT_TEMPLATE_SUFFX).replaceAll("/+","/"));
        return new View(templateFile);
    }
}
