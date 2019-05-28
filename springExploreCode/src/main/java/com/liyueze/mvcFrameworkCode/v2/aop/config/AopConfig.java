package com.liyueze.mvcFrameworkCode.v2.aop.config;

import lombok.Data;


@Data
public class AopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

    //正则表达式的pointCut
    private String pointCutRegex;

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
        parse();
    }

    /**
     * 解析配置文件中的匹配类的正则表达式
     */
    public void parse() {
        String pointCut= this.pointCut;
        //pointCut=public .* com.liyueze.demo.v2.service..*Service..*(.*)
        if(null!=pointCut){
            //正则表达式中匹配.是/.
            //替换后public .* com\.liyueze\.demo\.v2\.service\..*Service\..*\(.*\)
            pointCut=pointCut.replaceAll("\\.","\\\\.")
                    .replaceAll("\\\\.\\*",".*")
                    .replaceAll("\\(","\\\\(")
                    .replaceAll("\\)","\\\\)");
            this.pointCutRegex=pointCut;
        }
    }

}
