<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
		http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd">

    <!-- UID generator -->
    <bean id="disposableWorkerIdAssigner" class="com.baidu.fsg.uid.worker.DisposableWorkerIdAssigner"/>

    <bean id="uidGenerator" class="com.baidu.fsg.uid.impl.DefaultUidGenerator" lazy-init="false">
        <property name="workerIdAssigner" ref="disposableWorkerIdAssigner"/>

        <!-- Specified bits & epoch as your demand. No specified the default value will be used -->
        <property name="timeBits" value="29"/>
        <property name="workerBits" value="21"/>
        <property name="seqBits" value="13"/>
        <property name="epochStr" value="2016-09-20"/>
    </bean>

    <!-- Mybatis Mapper扫描 -->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="annotationClass" value="org.springframework.stereotype.Repository" />
        <property name="basePackage" value="com.baidu.fsg.uid.worker.dao" />
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
    </bean>

</beans>