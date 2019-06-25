# SpringExplore
* SpringJDBC事务
    * 什么是事务：逻辑上的执行单元，要么都执行要么都不执行
    * 事务的特性（ACID）
        * A : atomicity 原子性
        * C : consistency 一致性
        * I : isolation 隔离性
        * D : durability 持久性
        
    * 事务带来的问题   
        ```
           1.脏读：(修改中读取)
                一次事务修改数据，还未commit，这时另一个事务访问该数据,并读取了该未修改的数据。
                那么另外一个事务读到的这个数据是“脏数据”，依据“脏数据”所做的操作可能是不正确的
           2.不可重复读：（读取中修改）
                一次事务需要读取两次数据，这时一个事务对该数据进行了修改。
                这时两次读取的数据是不一致的
           3.丢失修改（修改中又修改）
               一次事务中正在修改数据，这时另个事务也来修改该数据。
               最后形成的数据会丢失一次修改
           4.幻读（批量修改中新增）
                一次事务正在做批量修改，这时另一个事务插入了一些新数据。
                第一次事务就会丢失对于新数据的修改
   
        ```   
   * springJDBC 事务隔离级别 
     ```
     xml的配置见 application-aop.xml文件中
     
     以下为注解的使用
       1.@Transactional(isolation = Isolation.DEFAULT)
            后端数据库默认的隔离级别。
            例如：Mysql 默认采用的 REPEATABLE_READ隔离级别。
                Oracle 默认采用的 READ_COMMITTED隔离级别.  
       2.@Transactional(isolation = Isolation.READ_UNCOMMITTED)
            什么都允许
            允许读取尚未提交的数据变更，可能会导致脏读、幻读或不可重复读
       4.@Transactional(isolation = Isolation.READ_COMMITTED)
            阻止修改中读取
            允许读取并发事务已经提交的数据，可以阻止脏读，但是幻读或不可重复读仍有可能发生
       3.@Transactional(isolation = ISOLATION.REPEATABLE_READ)
            repeatbale_read 阻止读取中修改（除非是一次事务）
            对同一字段的多次读取结果都是一致的，除非数据是被本身事务自己所修改。
            可以阻止脏读和不可重复读，但幻读仍有可能发生。
       4.@Transactional(isolation = Isolation.SERIALIZABLE)
           完全阻止
           可以防止脏读、不可重复读以及幻读。但是这将严重影响程序的性能。
           通常情况下也不会用到该级别。
       
        ```
   * 事务传播行为 （为了解决业务层方法之间互相调用的事务问题）
        ```
        只列举常见的四种事务传播级别
        
        xml的配置见 application-aop.xml文件中
        
        以下为注解的使用
        支持当前事务：
          1.@Transactional(propagation = Propagation.REQUIRED )
               如果当前存在事务，则加入该事务；
               如果当前没有事务，则创建一个新的事务。
          2.@Transactional(propagation = Propagation.SUPPORTS)
               如果当前存在事务，则加入该事务；
               如果当前没有事务，则以非事务的方式继续运行。 
        不支持当前事务：
          3.@Transactional(propagation = Propagation.REQUIRES_NEW)
               创建一个新的事务，如果当前存在事务，则把当前事务挂起。
          4.@Transactional(propagation = Propagation.NOT_SUPPORTED)
               以非事务方式运行，如果当前存在事务，则把当前事务挂起。
          
           ```
* 分布式事务  
  从ACID到CAP  
  在分布式系统中，同时满足 CAP 定律中的一致性 Consistency、可用性 Availability 和
  分区容错性 Partition Tolerance 三者是不可能的。在绝大多数的场景，都需要牺牲强一
  致性来换取系统的高可用性，系统往往只需要保证最终一致性。