AI Code  自动生成框架代码 节约30%的时间
###1.运行方法：
请修改项目中的jdbc.properties 文件将数据库链接修改成自己的数据地址即可
如:jdbc.url=jdbc:mysql://192.168.10.250:3306/ai_code_simple?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=round&allowMultiQueries=true
前提是执行了ai-code.sql 脚本，执行后会有原始数据被导入，用于生成过程中的必要数据，请务必保证数据完整未被修改


###2.编写模板说明：
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
