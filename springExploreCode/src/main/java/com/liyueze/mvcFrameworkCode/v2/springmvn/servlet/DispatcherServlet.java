package com.liyueze.mvcFrameworkCode.v2.springmvn.servlet;

import com.liyueze.mvcFrameworkCode.v2.annotation.ControllerV2;
import com.liyueze.mvcFrameworkCode.v2.annotation.RequestMappingV2;
import com.liyueze.mvcFrameworkCode.v2.contex.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class DispatcherServlet extends HttpServlet {
    ApplicationContext applicationContext;
    //存储所有handleMapping
    private List<HandlerMapping> handlerMappings = new ArrayList<>();
    //建立Handle和HandlerAdapter的联系
    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<>();

    private static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
    private static final String TEMPLATE_ROOT = "templateRoot";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //初始化application容器
        applicationContext = new ApplicationContext(servletConfig.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //初始化SpringMVC九大组件(只实现部分)
        initStrategies(applicationContext);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        //通过从request中拿到URL，去匹配一个HandlerMapping
        HandlerMapping handler = getHandler(req);
        if(handler == null){
            Model model=new Model();
            processDispatchResult(req,resp,new ModelAndView("404",model));
            return;
        }
        //根据handleMapping找到对应的handleAdapter
        try {
            HandlerAdapter handlerAdapter=handlerAdapters.get(handler);
            ModelAndView modelAndView=null;
            if(handlerAdapter!=null){
                modelAndView=handlerAdapter.handle(req,resp,handler);
            }
            //正常返回
            processDispatchResult(req, resp, modelAndView);
        } catch (Exception e) {
            //异常返回
            Model model=new Model();
            model.put("detail","服务器出现了异常");
            model.put("stackTrace",e.getStackTrace());
            processDispatchResult(req,resp,new ModelAndView("500",model));
            e.printStackTrace();
        }
    }

    //根据modelAndView找到对应的viewResolver
    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView modelAndView) {
        if(null==modelAndView){
            return;
        }
        for(ViewResolver viewResolver:viewResolvers){
            try {
                String viewName=modelAndView.getViewName();
                if(viewResolver.support(viewName)){
                    viewResolver.resolveViewName(viewName,null).render(modelAndView.getModel().getModel(),req,resp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        HandlerMapping matchHandleMapping = null;
        for (HandlerMapping handlerMapping : handlerMappings) {
            if (url.matches(handlerMapping.getPattern().pattern())) {
                matchHandleMapping = handlerMapping;
            }
        }
        return matchHandleMapping;
    }

    private void initStrategies(ApplicationContext applicationContext) {
        //多文件上传的组件
        initMultipartResolver(applicationContext);
        //初始化本地语言环境
        initLocaleResolver(applicationContext);
        //初始化模板处理器
        initThemeResolver(applicationContext);
        //handlerMapping，实现
        initHandlerMappings(applicationContext);
        //初始化参数适配器，实现
        initHandlerAdapters(applicationContext);
        //初始化异常拦截器
        initHandlerExceptionResolvers(applicationContext);
        //初始化视图预处理器
        initRequestToViewNameTranslator(applicationContext);
        //初始化视图转换器，实现
        initViewResolvers(applicationContext);
        //转发参数缓存器（重定向）
        initFlashMapManager(applicationContext);

    }


    //建立controller和URl的映射关系
    private void initHandlerMappings(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object instance = applicationContext.getBean(beanName);
            Class clazz = instance.getClass();
            if (null == clazz.getAnnotation(ControllerV2.class)) {
                continue;
            }
            String baseUrl="";
            if (clazz.isAnnotationPresent(RequestMappingV2.class)) {
                RequestMappingV2 requestMappingController = (RequestMappingV2) clazz.getAnnotation(RequestMappingV2.class);
                baseUrl="/" + requestMappingController.value();
            }
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(RequestMappingV2.class)) {
                    continue;
                }
                RequestMappingV2 requestMappingV2Method =method.getAnnotation(RequestMappingV2.class);
                String url=baseUrl+"/" + requestMappingV2Method.value();
                String regx = url.replaceAll("\\*", ".*").replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regx);
                handlerMappings.add(new HandlerMapping(pattern, instance, method));
            }
        }

    }

    /**
     * 因为一个request的发送过来的参数都是String类型的，需要根据方法对应的参数自动转型（这就是HandlerAdapter的作用）
     * 因为在Spring中有各种HandlerMapping，所以有很多适配的不同handler的HandleAdapter
     * 这里简化，只做了URl映射关系的HandlerMapping,所以也只有一个handlerAdapter
     *
     * @param applicationContext
     */
    private void initHandlerAdapters(ApplicationContext applicationContext) {
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            //本来这里应该有根据不同的handlerMapping选择不同handleAdapter的逻辑。
            // 现在因为简化成一个handle所以就直接handlerAdapter了
            HandlerAdapter handlerAdapter=new HandlerAdapter();
            if(handlerAdapter.supports(handlerMapping)){
                this.handlerAdapters.put(handlerMapping, handlerAdapter);
            }
        }
    }

    /**
     * ViewResolver的作用是将返回的modelAndView匹配相应的view
     * Spring中有很多种的viewResolver，需要在xml中配置
     * 而在这里的实现中只有一种viewResolver且还在properties文件中配置，所以其实不需要用一个list去保存
     * 但是为了模仿还是用了list去保存。每次这个list中只有一个值
     *
     * @param applicationContext
     */
    private void initViewResolvers(ApplicationContext applicationContext) {
        List<Properties> configs = applicationContext.getConfig();
        for (Properties config : configs) {
            String templateRoot = config.getProperty(TEMPLATE_ROOT);
            viewResolvers.add(new ViewResolver(templateRoot));
        }
    }

    private void initHandlerExceptionResolvers(ApplicationContext applicationContext) {
    }

    private void initRequestToViewNameTranslator(ApplicationContext applicationContext) {
    }

    private void initFlashMapManager(ApplicationContext applicationContext) {
    }

    private void initMultipartResolver(ApplicationContext applicationContext) {
    }

    private void initLocaleResolver(ApplicationContext applicationContext) {
    }

    private void initThemeResolver(ApplicationContext applicationContext) {
    }
}
