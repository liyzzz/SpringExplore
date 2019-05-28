package com.liyueze.mvcFrameworkCode.v2.aop.support;

import com.liyueze.mvcFrameworkCode.v2.aop.config.AopConfig;
import com.liyueze.mvcFrameworkCode.v2.aop.intercepter.AfterReturningAdviceInterceptor;
import com.liyueze.mvcFrameworkCode.v2.aop.intercepter.AfterThrowingAdviceInterceptor;
import com.liyueze.mvcFrameworkCode.v2.aop.intercepter.AspectAdviceTemplate;
import com.liyueze.mvcFrameworkCode.v2.aop.intercepter.MethodBeforeAdviceInterceptor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 将代理需要的基本参数保存下来并解析
 * 生成拦截器
 */

@Data
public class AdvisedSupport {
    //存AOP的配置信息
    private AopConfig aopConfig;
    //被代理的类
    private Class<?> targetClass;
    //被代理的对象
    private Object target;

    //每个方法的拦截器链
    private transient Map<Method, List<AspectAdviceTemplate>> methodCache=new HashMap<>();
    //切面对象
    private Object aspect;



    public AdvisedSupport(AopConfig aopConfig, Object aspect) {
        this.aopConfig = aopConfig;
        this.aspect = aspect;
    }


    /**
     * @param method
     * @param targetClass
     * @return 返回拦截器链
     * @throws Exception
     */
    public List<AspectAdviceTemplate> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        parse();
        List<AspectAdviceTemplate> cached = methodCache.get(method);
        //因为这里传进来的有可能是接口的某个方法，这个时候就需要根据实例的class方法重新获取
        if(cached==null){
            //获取接口实现类的该方法
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = methodCache.get(m);
        }
        return cached;
    }

    /**
     * 解析拦截器链
     */
    private void parse() {
        if (targetClass == null || target == null || aopConfig == null || aspect == null || aopConfig.getPointCut() == null) {
            return;
        }
        String pointCut = aopConfig.getPointCutRegex();

        try {
            Class aspectClass=Class.forName(aopConfig.getAspectClass());
            //保存切面中所有方法，重载的方法只会执行一个（方法名相同会覆盖）
            Map<String,Method> aspectMethods = new HashMap<String,Method>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }

            for(Method method:targetClass.getMethods()){

                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                if(methodString.matches(pointCut)){
                    //拦截器链
                    List<AspectAdviceTemplate> advices = new LinkedList<>();
                    String before=aopConfig.getAspectBefore();
                    if(!(null == before || "".equals(before))){
                        advices.add(new MethodBeforeAdviceInterceptor(aspectMethods.get(before),aspect));
                    }
                    String afterThrow=aopConfig.getAspectAfterThrow();
                    if(!(null == afterThrow || "".equals(afterThrow))) {
                        advices.add(new AfterThrowingAdviceInterceptor(aspectMethods.get(afterThrow),aspect));
                    }
                    String after=aopConfig.getAspectAfter();
                    if(!(null == after || "".equals(after))) {
                        advices.add(new AfterReturningAdviceInterceptor(aspectMethods.get(after),aspect));
                    }
                    Collections.sort(advices);
                    methodCache.put(method,advices);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断该对象是否符合pointCut的规则
     *
     * @return
     */
    public boolean pointCutMatch() {
        if (aopConfig.getPointCutRegex() == null || this.targetClass == null) {
            return false;
        }
        String pointCut=aopConfig.getPointCutRegex();
        //   public .* com\.liyueze\.demo\.v2\.service\..*Service
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        //   com\.liyueze\.demo\.v2\.service\..*Service
        String pointCutClassPattern=pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1);
        return this.targetClass.getName().matches(pointCutClassPattern);
    }


}
