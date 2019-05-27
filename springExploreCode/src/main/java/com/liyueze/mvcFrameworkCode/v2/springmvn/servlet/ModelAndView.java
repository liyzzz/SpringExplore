package com.liyueze.mvcFrameworkCode.v2.springmvn.servlet;

import lombok.Data;

import java.util.Map;

@Data
public class ModelAndView {
    //这是存放视图名
    private String viewName;
    //modle其实就是一个map用来存放controller方法的返回值
    private Model model;

    public ModelAndView(String viewName) { this.viewName = viewName; }

    public ModelAndView(String viewName, Model model) {
        this.viewName = viewName;
        this.model = model;
    }
}
