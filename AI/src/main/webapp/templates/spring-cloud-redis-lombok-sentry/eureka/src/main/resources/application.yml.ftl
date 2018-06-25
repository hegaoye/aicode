spring:
#  profiles: dev                                 #当使用配置文件管理中心时有用在核心配置文件里面的spring.profiles.active=dev这个设置表示系统默认使用dev（即application-dev.properties）中的配置,如果需要使用测试环境的配置文件(application-test.properties),只需修改: spring.profiles.active=test 即可
  application:
    name: Eureka-Server
  output:
     ansi:
       enabled: always #控制台输出颜色配置

server:
  port: 8761 #端口号

eureka:
  instance:
    hostname: localhost #指定主机名
  client:
    registerWithEureka: false #是否将自身注册
    fetchRegistry: false #如果为true，启动时报警.
    serviceUrl:
      defaultZone: http://${r'${eureka.instance.hostname}'}:${r'${server.port}'}/eureka/
  server:
    enable-self-preservation: true


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
