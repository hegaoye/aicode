# AI Code
技术交流 qq 群：839360512
项目支持``docker``启动方式，``war``启动方式，轻松快速上手，
项目已经经历了10多个项目，其中包含融资项目，模板几经修改和整合，很实用，新的功能按部就班添加中，期待大家的反馈和使用感受
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
### 特点
ai-code 与其他开源的完整项目不同点在于技术整合框架的抽象，mvc的模型抽象，我们有这样的场景，有新的业务需求，用同样的技术框架，通常做法可能就是找一个项目然后删删改改提取
出来一个较为干净的框架，然后把新的业务模型，实体类，接口等等重新声明，创建在此基础上再做很多CRUD的编码，有时候我们很反感重复这些，但是又不得不做，通常做这些毫无意义又无聊
同意请举手，于是乎有没有办法做一个工具出来让新的业务模型设计好后拿到``sql``脚本 通过工具可以选择各种不同的技术框架组合，就像``spring boot``一样整合技术在一起，但是又
可以自动把``mybatis mapper,dao/dto,service,ctrl,ui``等等通用功能自动开发好呢？把常用的工具，加密算法等等通用的自动集成进去呢？比如``swagger,gradle,maven``等等
答案是肯定的``ai-code``就是这个思路，整合有给你定制的可能，业务随意变化，只要拿到sql脚本一切就是动动手指，点击生成，再也不用搞来搞去那些重复的不能再重复的无聊工作了。
这就是ai-code的基本思路和思想；

再来看下通常的开源思路是什么呢？
完整项目，比如``RBAC``模型下的管理系统，有权限，区域，部门，日志等等，这些无可厚非确实通用，但毕竟如果要和自己的项目融合是要调整的，而且编码习惯不同，
设计规则可能不满意，维护过慢等等问题，但是也是可以使用的，于是乎就有了各种 商城开源，权限管理开源，区域管理开源，可以关注学习没问题，非常棒，甚至直接用。

两种思路从不同的角度来看待重用问题，ai-code 只希望抛砖引玉，让抽象思维，整合思维，服务未来思维体现出来，期待更好地项目出现，基于ai-code,期待大家贡献
技术模板，让那个更多人从中受益，感谢大家的贡献。

# 使用方法
### 连接数据库：
-首先要做的就是建立``ai_code``的数据库(推荐数据库格式：编码为utf8mb4  排序为 utf8mb4_general_ci)，建议使用``mysql``,脚本请用最新的版本日期，你可以在项目根目录下``sql/aicodexxxxx.sql``中找到合适的
sql脚本,比如创建数据库名为``ai_code``,数据库的ip为``192.168.1.220``,账户密码为``username=root,password=xxxxx``;

-找到项目下 ``AI/src/main/resources/jdbc.properties`` 文件将数据库链接修改成刚才初始化的数据库``ip``
```
jdbc.url=jdbc:mysql://192.168.1.220:3306/ai_code?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=round&allowMultiQueries=true
jdbc.username=root
jdbc.password=xxxxx
```

修改完毕后可以直接启动,启动默认为``8080``端口
浏览器中打开输入：``http://127.0.0.1:8080/index.html``
默认账户:``admin  888888``尝试登录，你可以在数据库中修改次账户数据表明为``account``
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/index.png)

参考已有项目的效果
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/aicode_index.png)

### 模板仓库添加
在你登陆后你可以添加自己的模板仓库

![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/aicode_templates.png)

### 创建项目
有了模板之后可以创业见一个项目，然后按照``下一步``的指引操作下去即可完成操作
(此处为gif动画需要你登录账户才能看，osc的限制)
![image text](https://gitee.com/helixin/AI-Code/raw/dev/images/show.gif)

### 构建项目
构建很简单，点击即可自动完成对项目的生成，下载模板，生成代码，提交到git仓库，这个地方的日志输出是websocket，存储文档log可以反复查看
十分方便
(此处为gif动画需要你登录账户才能看，osc的限制)
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/build.gif)

之后点击``构建项目`` 按钮就开始进行想的自动构建了

### 启动项目
当你生成项目代码后你可以在你的``git svn``仓库中找到源码，也可以直接下载源码自行导入到开发工具中，然后运行，这里需要注意的是配置文件和
数据库的连接一定要修改，否则默认的将无法使用，一下为测试演示的界面图供参考
代码界面和运行启动项目 我用的是Idea 最好用的java开发工具，没有之一，开发用就用最好的工具，不将就
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/running.png)

swagger界面如下，我只用了一个简单的表来演示，你的要有多复杂就有多复杂均可以实现
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/swagger.png)

如果你体验了高效的代码生成和抽象思维把规律的重复的代码进行模板化后可以服务你未来更久的时间，那时候就体验到，编程除了用coding还可以用设计
来编码，所谓武功至高境界就是``无招胜有招`` 就是这个意思，让代码服务你更久才有时间学习更多的技术和知识，让今天的一行代码在未来体现出更大价
值才是我们要思考和做的，期待大家的分享和模板的壮大，目前正在整理``rabbitmq, kafka, rocketmq``的分布式，微服务框架模板整合，后面将会
更好玩哦，模板链接再次推荐可以参考：https://gitee.com/helixin/aicode_template
祝你和你的团队编程愉快，coding让世界更美好。

### docker 启动方式(推荐)
docker 依赖dockerhub 镜像（意思是你的网络要支持访问哦，大家明白哈，搞不定的加群），大家可以通过docker的常规命令``pull`` ``run`` 即可，注意：docker镜像
中不包含数据库，数据库脚本请从项目中下载进行初始化，数据库名称请使用``ai_code``命名，以下docker命令操作
dockerhub 详细操作说明 https://hub.docker.com/r/hegaoye/aicode

```
#检出数据库
docker pull hegaoye/mysql:1.0-beta
#启动数据库 设置 --hostname=aicode-db 用于link方式连接
docker run -p  3306:3306 -e MYSQL_ROOT_PASSWORD=aicode --hostname=aicode-db  --name aicode-db  --restart always -d hegaoye/mysql:1.0-beta  --lower_case_table_names=1

#搜索查看aicode的镜像是否存在
docker search aicode

#拉取aicode的镜像 hegaoye/aicode:1.0-beta
docker pull hegaoye/aicode:1.0-beta

#启动容器,注意 host,username,passowrd 要进行修改成自己的mysql主机 使用link 连接aicode-db
docker run --link aicode-db:aicode-db -e host=aicode-db:3306 -e username=root -e password=aicode -p 8080:8080 --name aicode --restart always -d  hegaoye/aicode:1.0-beta

#查看日志
docker logs --tail 1000 -f aicode

#重启，关闭，启动容器命令
docker restart|stop|start aicode
```


### 编写模板说明：
模板语法采用freemarker编写，定义了一个实体类可以在项目``io.aicode.base.core.TemplateData`` 下找到此类，大致的内置变量可以如下图

![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/aicode_help.png)

如果你需要编写自己的模板按照这里面的规则即可完成编写，如果需要参考已有模板请看顶部的连接分享你可以根据自己的需求修改一个出来然后也分享出来让大家
享受你的技术带来的便捷。
### UML参考
uml信息可以自己打开vpp文件查看详细，仅仅放两张用于参考
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/models.jpg)

![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/active.jpg)

### 模板长啥样？
模块化模板样貌
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/tmp4.png)

Ctrl模板类
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/tmp1.png)

mybatis sql 模板注意观察有关联关系哦
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/tm2.png)

前端模板抽离样貌
![Image text](https://gitee.com/helixin/AI-Code/raw/dev/images/tmp3.png)



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

这个sql在执行中失败，失败的原因在于 ``"order"`` 双引号不合法将无法执行，所以请检查后修改为``order`` 的合法sql语句，最好在生成前执行验证sql
脚本的正确性，比如下面的

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

模板仓库地址 https://gitee.com/helixin/aicode_template

docker教程 https://hub.docker.com/r/hegaoye/aicode