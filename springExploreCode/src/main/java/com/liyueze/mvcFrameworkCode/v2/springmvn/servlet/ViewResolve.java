package com.liyueze.mvcFrameworkCode.v2.springmvn.servlet;

import lombok.Data;

import java.io.File;
import java.net.URL;

/**
 * 保存配置文件中配置的视图的根文件夹和视图后缀
 * 根据模板适配不同的view
 */
@Data
public class ViewResolve {
    //后缀
    private String templateSuffx;
    //根文件
    private File templateRootDir;

    public ViewResolve(String templateSuffx, String templateRoot) {
        this.templateSuffx = templateSuffx;
        URL url=this.getClass().getClassLoader().getResource(templateRoot);
        if(null!=url){
            this.templateRootDir = new File(url.getFile());
        }
    }
}
