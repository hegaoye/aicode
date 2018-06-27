spring:
  application:
    name: ${projectName}-consumer
  output:
    ansi:
      enabled: always
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

feign:
  hystrix:
    enabled: true

hystrix:
   command:
      default:
         execution:
           isolation:
             thread:
               timeoutInMilliseconds: 3000 #hystrix调用方法的超时时间，默认是1000毫秒


server:
  port: 8764

#访问注册中心配置
eureka:
   instance:
      status-page-url: http://localhost:${r'${server.port}'}/swagger-ui.html
      lease-expiration-duration-in-seconds: 5 #心跳更新时间5s
      lease-renewal-interval-in-seconds: 5 #心跳过期时间5s
   client:
     serviceUrl:
       defaultZone: http://localhost:8761/eureka/

logging:
#   pattern:
#     console: "%d{HH:mm:ss.SSS} [%t] %-5p: %m%n"
   level:
     com:
       pitop: debug

   path: /var/log/tomcat/
#     org:
#      springframework: debug
#      spring:
#        springboot:
#               dao: debug


