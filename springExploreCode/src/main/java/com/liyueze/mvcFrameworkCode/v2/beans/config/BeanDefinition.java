package com.liyueze.mvcFrameworkCode.v2.beans.config;

import lombok.Data;

@Data
public class BeanDefinition {
    private String beanName;

    public static void main(String[] args) {
        int[][] a={{0,1},{0,0}};
        double count1=0;//第一次取得红球的次数
        double count2=0;//第二次取得红球的次数
        for(int i=0;i<10000000;i++){
            int random1=(int)(Math.random()*2);//那个盒子
            int random2=(int)(Math.random()*2);//那个求
            if(a[random1][random2]==0){
                count1+=1.0;
                if(a[random1][1-random2]==0){
                    count2+=1.0;
                }
            }
        }
        System.out.println(count2/count1);
    }

    /*public static void main(String[] args) {
        int[][] a={{0,1},{0,0}};
        double count1=0;//第一次取得红球的次数
        double count2=0;//第二次取得红球的次数
        for(int i=0;i<10000000;i++){
            int random1=(int)(Math.random()*2);//那个盒子
            int random2=(int)(Math.random()*2);//那个求
            if(a[random1][random2]==0){
                count1+=1.0;
                //第二次
                int random3=(int)(Math.random()*2);//那个盒子
                if(random3==random1 && random1==1){
                    count2+=1;
                }
                if(random3!=random1){
                    int random4=(int)(Math.random()*2);//那个求
                    if(a[random1][random4]==0){
                        count2+=1.0;
                    }
                }

            }
        }
        System.out.println(count2/count1);
    }*/

}
