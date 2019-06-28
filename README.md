# AI Code
一个基于freemarker为核心的代码生成框架，本项目目前支持sql进行反向生成java的代码，代码生成的程度取决于用的模板，目前整合的模板中
有如下：
```
- ssm+redis+swagger+lombok
- springboot-redis-swagger-lombok
- ssm+dubbo+redis+swagger+lombok
- ssh+redis+swagger+lombok
- spring-cloud-redis-lombok-sentry
- angular-template
- springboot-redis-swagger-lombok-frontend
- angular-template-i18n
```
模板分享地址: https://gitee.com/helixin/aicode_template
以上框架为基本常用的框架，目前只需要准备好你的sql脚本在此项目页上进行创建即可，然后``aicode`` 会自动帮你把数据库创建出来用于反向生成代码使用，
自动从你的模板仓库中下载框架，当然也可以是用我们已经开发好的，代码会从``dao->service->ctrl->frontend`` 一条龙生成，接口自动根据规则对接完
毕，也就是说你可以生成后直接运行你的项目代码，另外项目代码将会根据你的需要配置上``git svn`` 地址后自动提交到对应的仓库中省去手工拷贝的麻烦，只
需要在开发工具中 ``git clone git://xxxxx.git``即可，如果模板比较优秀可以节省代码50%左右无需再开发，比如包含了：
``增，删，查，改，分页，条件查询，关联查询，关联分页查询``
等等，你需要关心的只是你的逻辑关系是否设置完整，模型关系是否设置完整用于生成时系统进行自动化，我们提供了``web-ui``管理界面，只需要登录账户即可
完成对代码的生成管理，你可以通过页面来完成模型关系的设置，以及对显示属性的设置，快速完成从数据库层到页面的定制化开发。

# 使用方法
### 1.连接数据库：
-首先要做的就是建立``aicode``的数据库，建议使用``mysql``,脚本请用最新的版本日期，你可以在项目根目录下``sql/aicodexxxxx.sql``中找到合适的
sql脚本,比如创建数据库名为``aicode``,数据库的ip为``192.168.1.220``,账户密码为``username=root,password=xxxxx``;

-找到项目下 ``AI/src/main/resources/ai_jdbc.properties`` 文件将数据库链接修改成刚才初始化的数据库``ip``
```
jdbc.url=jdbc:mysql://192.168.1.220:3306/aicode?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=round&allowMultiQueries=true
jdbc.username=root
jdbc.password=xxxxx
```

修改完毕后可以直接启动,启动默认为``8080``端口
浏览器中打开输入：``http://127.0.0.1:8080/index.html``
默认账户:``lixin  888888``尝试登录，你可以在数据库中修改次账户数据表明为``account``

![Image text](https://gitee.com/helixin/AI-Code/raw/feature/displayAndRelation/images/index.png)


### 2.编写模板说明：
模板采用freemarker 
模板可以拿到如下数据：
${projectName} //项目英文名
${model}   //模块中的模型 -> ${basePackage}.${model}.service
${module} //模块 一个项目的模块化 不参与java的包定义只是项目管理分离办法

${basePackage}  //包名

${table}  //表对象
${tableName}  //表名

${classes}  //类信息对象  集合
${class}  //类对象
${className}  //类名
${classNameLower}  //类名小写

${columns}  //列对象  集合
${pkColumns}  //主键数据信息
${notPkColumns}  //非主键数据信息


${fields}  //类属性  集合
${pkFields}  //主键数据信息
${notPkFields}  //非主键主键数据信息

${notes}  //类注释
${copyright}  //项目版权
${author}  //作者
