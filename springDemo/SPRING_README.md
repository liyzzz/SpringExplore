# SpringExplore
* Spring面试中的常见的问题
    * 1、什么是 Spring 框架？Spring 框架有哪些主要模块？  
    Spring帮助开发者解决开发中基础性问题，使得开发者可以专注于应用程序的开发。   
    Spring框架至今集成了20多个模块。这些模块主要被分为：核心容器,数据访问，web,aop,工具，消息和测试模块
    * 2、使用 Spring 框架能带来哪些好处？
    对象之间的依赖关系一目了然（DI依赖注入）  
    以模块的形式来阻止，只需按需添加需要的模块就可以了  
    测试简单  
    轻量简约
    * 3、什么是控制反转(IOC)？什么是DI(依赖注入)？
    IOC: 对象的管理交给程序自己去管理。目的是为了解耦
    DI:依赖注入是IOC的一种实现手段
    * 4、在 Spring 中依赖注入有哪些方式？  
     set方法注入（常见的成员变量），构造器注入（构造方法），接口注入（成员变量为接口时）
    * 5、BeanFactory 和 ApplicationContext 有什么区别？  
    首先ApplicationContext是BeanFactory的子类。也就意味着是对BeanFactory的拓展  
    BeanFactory是对bean生命周期的控制  
    ApplicationContext在以上的功能上还支持   
    1.国际化 2.统一的资源文件读取方式 3.监听(Aware接口)
    * 6、Spring 提供几种配置方式来设置元数据？  
    1.基于 XML 的配置 2.基于注解的配置 3.基于 Java 的配置（
@Configuration，@Bean）
    * 7、 Spring Bean 的生命周期？  
    这个应该根据Bean的作用域来回答  
   1.singleton：和spring容器同生共死  
   2.prototype：需要时创建，对象不可达则被回收    
   3.request：一次request中存活。为每一个来自客户端的网络请求创建一个实例，在请求完成以后，bean会失效并被垃圾回收器回收      
   4.Session：与session一致      
   5.global-session： 与 Portlet 应用相关。地位相当于 Servlet 中的 session     
   * 8、什么是Spring inner beans？  
    如果一个Bean仅应用于另个Bean中，则应该以内部bean的形式出现,类似于内部类
    * 9、Spring 框架中的单例 Beans 是线程安全的么？  
    从spring的角度来说，spring没有对多线程的线程安全做任何封装。每个Bean的线程安全都是由开发者自己去考虑。   
    从线程安全的角度来说，只有Bean为单例并且在对单例做修改时才会有线程安全问题。一般来说像controller，dao,service等Bean都是没有可变化状态的，所以某种程度上来说Bean是安全的。
    * 10、在 Spring 中可以注入 null 或空字符串吗？  
    完全可以
    
