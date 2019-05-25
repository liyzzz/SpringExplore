package com.liyueze.mvcFrameworkCode.v1.servlet;

import com.liyueze.mvcFrameworkCode.v1.annotation.AutowiredV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.ControllerV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.RequestMappingV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.RequestParamV1;
import com.liyueze.mvcFrameworkCode.v1.annotation.ServiceV1;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DispatcherServletV1 extends HttpServlet {

    //保存application.properties配置文件中的内容
    private Properties contextConfig = new Properties();
    //ioc容器
    private Map<String, Object> ioc = new HashMap<>();
    //handleMapping中记录的是url和method的关系
    private Map<String, Method> handleMapping = new HashMap<>();

    /**
     * 重写init方法
     * 完成对spring mvc的初始化
     * 既：
     * 1.加载配置文件
     * 2.扫描类并将其加入到ioc容器中
     * 3.对ioc容器中的类进行di操作（对autowired进行自动注入）
     * 4.初始化handleMapping
     *
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //扫描相关的类并初始化到ioc容器中
        String packageName = contextConfig.getProperty("scanPackage");
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        if(url==null){
            return;
        }
        doInstance(new File(url.getFile()),packageName);
        //进行Di操作，将ioc容器中类的成员变量自动注入
        doAutowired();
        //初始化handleMapping，既url和方法的映射关系
        doHandleMapping();
        System.out.println("GP Spring framework is init.");
    }

    private void doHandleMapping() {
        ioc.forEach((key, value) -> {
            String baseUrl = "";
            if (value.getClass().isAnnotationPresent(RequestMappingV1.class)) {
                RequestMappingV1 requestMappingV1 = value.getClass().getAnnotation(RequestMappingV1.class);
                baseUrl = requestMappingV1.value();
            }
            //只获取公开的方法
            Method[] methods = value.getClass().getMethods();
            for (Method method : methods) {
                if(!method.isAnnotationPresent(RequestMappingV1.class)){
                    continue;
                }
                RequestMappingV1 requestMappingV1=method.getAnnotation(RequestMappingV1.class);
                handleMapping.put((baseUrl+"/"+requestMappingV1.value()).replaceAll("/+","/"),method);
            }
        });
    }

    private void doAutowired() {
        //Lambda表达式遍历ioc容器
        ioc.forEach((key, value) -> {
            try {
                //获取所有申明的成员变量，不论是不是public
                Field[] fields = value.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(AutowiredV1.class)) {
                        continue;
                    }
                    //如果用户没有自定义beanName，就按类型注入
                    AutowiredV1 autowiredV1 = field.getAnnotation(AutowiredV1.class);
                    //设置强制访问
                    field.setAccessible(true);
                    String beanName = autowiredV1.value().trim();
                    //如果有设置beanName
                    if (beanName != null && !beanName.equals("")) {
                        Object objectValue = ioc.get(beanName);
                        if (objectValue == null) {
                            throw new Exception("Not Fount Bean Name");
                        }
                        //向obj对象的这个Field设置新值value
                        //第一个参数是obj对象第二个参数是新的value值
                        field.set(value, objectValue);
                    } else {
                        Object objectValue = ioc.get(field.getName());
                        if (objectValue != null) {
                            //按照类型注入(接口)
                            field.set(value, objectValue);
                        } else {
                            //不是接口的就按照类型小写注入
                            field.set(value, ioc.get(toLowerFirstCase(field.getName())));
                        }
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        });
    }

    /**
     * 扫描类并初始化到ioc容器中
     */
    private void doInstance(File f, String packageName) {
        if (f.isDirectory()) {
            File[] files=f.listFiles();
            for(File file:files){
                doInstance(file,packageName+"."+file.getName());
            }
        } else {
            //这个时候就已经是class文件了，而不是java文件
            if (!f.getName().endsWith(".class")) {
                return;
            }
            //类名就是包名加文件名
            String className =packageName.replace(".class", "");
            addIoc(className);
        }
    }

    /**
     * 添加到ioc容器中
     *
     * @param className
     */
    private void addIoc(String className) {
        try {
            Class clazz = Class.forName(className);
            /**
             * 如果有controller注解和service注解就添加的ioc容器中
             * 但是这里有一问题。
             * ioc容器的key值放什么
             * spring中注入有两种种：1.按名称注入（Resource）2.按类型注入（Autowired）
             * 按名称注入时，ioc的key应该为用户指定，若用户未指定则类名小写
             * 按类型注入的时候，当前类有多个接口，应该每个类的接口名称都当做可以放入到ioc容器中
             * 但是这样就会存在一个对象在ioc中有多个key情况，所以目前这个版本的对ioc容器初始化的操作待以后重构
             */
            if (clazz.isAnnotationPresent(ControllerV1.class)) {
                ControllerV1 controllerV1 = (ControllerV1) clazz.getAnnotation(ControllerV1.class);
                String name = controllerV1.value();
                doPutIoc(clazz, name);
            } else if (clazz.isAnnotationPresent(ServiceV1.class)) {
                ServiceV1 serviceV1 = (ServiceV1) clazz.getAnnotation(ServiceV1.class);
                String name = serviceV1.value();
                doPutIoc(clazz, name);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param contextConfigLocation 配置文件路径名称
     */
    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = null;
        if (contextConfigLocation.matches("classpath[\\*]?:.*")) {
            is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation
                    .substring(contextConfigLocation.indexOf(':') + 1, contextConfigLocation.length()));
        }
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }

    }

    /**
     * 调用具体的执行
     * post调get方法
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    /**
     * 重写get方法
     * 更具请求对应的handleMapping转发到具体的方法并反射执行
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url=req.getRequestURI();
        Method method=handleMapping.get(url);
        if(method==null){
            resp.getWriter().write("404 Not Found!!!");
        }
        try {
            //获取参数列表
            Class<?>[] parameterTypes = method.getParameterTypes();
            //每个参数的注解
            //是个二维数组[那个参数][那个注解]
            Annotation[][] annotations = method.getParameterAnnotations();
            //存放具体的参数值
            Object[] paramValues=new Object[parameterTypes.length];
            int i=0;
            for( Class<?> clazz :parameterTypes){
                if (clazz == HttpServletRequest.class) {
                    paramValues[i] = req;
                } else if (clazz == HttpServletResponse.class) {
                    paramValues[i] = resp;
                    //这个版本只接受两种类型的参数
                }else if(clazz == String.class || clazz == Integer.class){
                    for(Annotation annotation:annotations[i]){
                        if(annotation.annotationType().equals(RequestParamV1.class)){
                            String paramName= ((RequestParamV1) annotation).value();
                            //例如请求为：http://localhost:8080/user/query/name=xm&name=lyz
                            //这样就会接收到一个数组
                            //req.getParameter(paramName)只会取得一个值
                            String[] parameterValues =req.getParameterValues(paramName);
                            //存放数组拼成的字符串
                            StringBuffer value = new StringBuffer();
                            for(String parameterValue:parameterValues){
                                value = value.append(parameterValue + ",");
                            }
                            //删除最后一个逗号
                            value.delete(value.length() - 1, value.length());
                            //将string转化成对应的类型放在参数值列表里
                            paramValues[i] = convert(clazz, value.toString());
                        }
                    }
                }
                i++;
            }
            //利用反射掉方法
            method.invoke(ioc.get(toLowerFirstCase(method.getDeclaringClass().getSimpleName())),paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void doPutIoc(Class clazz, String name) throws Exception {
        //按名称注入
        if (name != null && !name.equals("")) {
            ioc.put(name, clazz.newInstance());
        } else {
            ioc.put(toLowerFirstCase(clazz.getSimpleName()), clazz.newInstance());
        }
        Class[] classes = clazz.getInterfaces();
        if (classes.length > 0) {
            for (Class interfaceClass : classes) {
                //如果容器中已经存在这个接口的实例就报错，例如：接口A有两个实现类B和C，B和C都要放在IOC容器中，这个时候就会报错
                if (ioc.containsKey(interfaceClass.getName())) {
                    throw new Exception("The “" + interfaceClass.getName() + "” is exists!!");
                }
                ioc.put(interfaceClass.getName(), clazz.newInstance());
            }
        }
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    //url传过来的参数都是String类型的，HTTP是基于字符串协议
    //只需要把String转换为任意类型就好
    private Object convert(Class<?> type, String value) {
        //如果是int
        if (Integer.class == type) {
            return Integer.valueOf(value);
        }
        //其他 。。。
        return value;
    }
}
