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
sql脚本中板焊了模板，会在你``构建项目``时自动拉取到本地，构建代码完毕后将会删除，你可以开源你的模板到这个连接中，也可以自己建立私库自己用都可以，目的就是让重复的
代码写一次后用模板技术和抽象思维服务更多的项目

以上框架为基本常用的框架，目前只需要准备好你的sql脚本在此项目页上进行创建即可，然后``aicode`` 会自动帮你把数据库创建出来用于反向生成代码使用，
自动从你的模板仓库中下载框架，当然也可以是用我们已经开发好的，代码会从``dao->service->ctrl->frontend`` 一条龙生成，接口自动根据规则对接完
毕，也就是说你可以生成后直接运行你的项目代码，另外项目代码将会根据你的需要配置上``git svn`` 地址后自动提交到对应的仓库中省去手工拷贝的麻烦，只
需要在开发工具中 ``git clone git://xxxxx.git``即可，如果模板比较优秀可以节省代码50%左右无需再开发，比如包含了：``增，删，查，改，分页，条
件查询，关联查询，关联分页查询``等等，你需要关心的只是你的逻辑关系是否设置完整，模型关系是否设置完整用于生成时系统进行自动化，我们提供了``web-ui``管理界面，只需要登录账户即可
完成对代码的生成管理，你可以通过页面来完成模型关系的设置，以及对显示属性的设置，快速完成从数据库层到页面的定制化开发。

# 使用方法
### 连接数据库：
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
![Image text](https://gitee.com/helixin/AI-Code/raw/master/images/index.png)

参考已有项目的效果
![Image text](https://gitee.com/helixin/AI-Code/raw/master/images/aicode_index.png)

### 模板仓库添加
在你登陆后你可以添加自己的模板仓库

![Image text](https://gitee.com/helixin/AI-Code/raw/master/images/aicode_templates.png)

### 创建项目
有了模板之后可以创业见一个项目，然后按照``下一步``的指引操作下去即可完成操作

![Image text](https://gitee.com/helixin/AI-Code/raw/master/images/aicode_start.png)

创建后效果
![Image text](https://gitee.com/helixin/AI-Code/raw/master/images/aicode_build.png)

之后点击``构建项目`` 按钮就开始进行想的自动构建了

### 编写模板说明：
模板语法采用freemarker编写，定义了一个实体类可以在项目``io.aicode.base.core.TemplateData`` 下找到此类，大致的内置变量可以如下图

![Image text](https://gitee.com/helixin/AI-Code/raw/master/images/aicode_help.png)

如果你需要编写自己的模板按照这里面的规则即可完成编写，如果需要参考已有模板请看顶部的连接分享你可以根据自己的需求修改一个出来然后也分享出来让大家
享受你的技术带来的便捷。
### UML参考
uml信息可以自己打开vpp文件查看详细，仅仅放两张用于参考
![Image text](https://gitee.com/helixin/AI-Code/raw/master/images/models.jpg)

![Image text](https://gitee.com/helixin/AI-Code/raw/master/images/active.jpg)


### 项目技术点
- spring mvc
- mybatis
- freemarker
- websocket
- java reflection
- angular framework
- git,svn client
- jwt
- gradle


### 注意
关于自己的sql脚本比如如下
```
/*==============================================================*/
/* Table: goods                                                 */
/*==============================================================*/
create table goods
(
   id                   bigint not null auto_increment,
   code                 varchar(64) not null comment '编码',
   name                 varchar(32) comment '商品名',
   stock                bigint comment '库存',
   status               varchar(32) comment '状态',
   primary key (id, code)
);

alter table goods comment '产品';

/*==============================================================*/
/* Table: "order"                                               */
/*==============================================================*/
create table "order"
(
   id                   bigint not null auto_increment,
   code                 varchar(64) not null comment '编码',
   order_no             varchar(32) not null comment '订单号',   
   primary key (id, code, order_no)
);

alter table "order" comment '订单';
```

这个sql在执行中失败，失败的原因在于 ``"order"`` 双引号不合法将无法执行，所以请检查后修改为``order`` 的和发sql语句，比如下面的

```
/*==============================================================*/
/* Table: order                                               */
/*==============================================================*/
create table `order`
(
   id                   bigint not null auto_increment,
   code                 varchar(64) not null comment '编码',
   order_no             varchar(32) not null comment '订单号',   
   primary key (id, code, order_no)
);

alter table `order` comment '订单';
```

去处后即可合法使用，一定要保障sql可以正常执行再用来跑代码，否则会失败


# 子项目
前端参考地址 https://gitee.com/helixin/ai-code-frontend

设计文档地址 https://gitee.com/helixin/AI-Code-Doc