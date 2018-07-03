spring:
  application:
     name: ${projectName?replace("_","-")}-provider
  output:
       ansi:
         enabled: always #控制台的输出颜色控制
  datasource:
     name: mysql-data
     url: jdbc:mysql://192.168.1.220:3307/${projectName}?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=round&allowMultiQueries=true
     username: root
     password: mysqladmin
     driver-class-name: com.mysql.cj.jdbc.Driver
     type: com.alibaba.druid.pool.DruidDataSource # 使用druid数据源
     initialSize: 10 #初始化连接数量，最大最小连接数
     maxActive: 100
     minIdle: 3
     maxWait: 600000  #获取连接等待超时的时间
     removeAbandoned: true  #超过时间限制是否回收
     removeAbandonedTimeout: 180 #超过时间限制多长
     timeBetweenEvictionRunsMillis: 600000 #配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     minEvictableIdleTimeMillis: 300000 #配置一个连接在池中最小生存的时间，单位是毫秒
     validationQuery: SELECT 1 FROM DUAL #用来检测连接是否有效的sql，要求是一个查询语句
     testWhileIdle: true #申请连接的时候检测
     testOnBorrow: false #申请连接时执行validationQuery检测连接是否有效，配置为true会降低性能
     testOnReturn: false #归还连接时执行validationQuery检测连接是否有效，配置为true会降低性能
     poolPreparedStatements: true #打开PSCache，并且指定每个连接上PSCache的大小
     maxPoolPreparedStatementPerConnectionSize: 100
     connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
     filters: stat #属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：监控统计用的filter:stat 日志用的filter:log4j 防御SQL注入的filter:wall
  redis:
    database: 0
    host: 192.168.1.220
    port: 6379
#    password: 123456
    timeout:  0 # 连接超时时间（毫秒）
    pool:
      max-active: 8 # 连接池最大连接数（使用负值表示没有限制）
      max-wait: -1 # 连接池最大阻塞等待时间（使用负值表示没有限制）
      max-idle: 8 # 连接池中的最大空闲连接
      min-idle: 0 # 连接池中的最小空闲连接




#设定自己启动内置tomcat端口号
server:
  port: 8762


#访问注册中心配置
eureka:
   instance:
      status-page-url: http://localhost:${r"${"}server.port}/swagger-ui.html
      lease-expiration-duration-in-seconds: 5 #心跳更新时间5s
      lease-renewal-interval-in-seconds: 5 #心跳过期时间5s
   client:
     serviceUrl:
       defaultZone: http://localhost:8761/eureka/


#指定mybatis映射文件的地址
mybatis:
  mapper-locations: classpath:mapper/*/*.xml,classpath:/META-INF/mybatis/mapper/WORKER*.xml
  configLocation: classpath:mapper/mybatis-config.xml

logging:
   level:
     com:
       pitop: debug

   path: /var/log/tomcat/
#     org:
#      springframework: debug
#      spring:
#        springboot:
#               dao: debug

