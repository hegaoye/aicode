package com.aicode.config;

import jakarta.annotation.Resource;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局事务管理器
 *
 * @author aicode
 */
@Aspect
@Configuration
public class TransactionalAopConfig {
    /**
     * 配置方法过期时间，默认-1,永不超时
     */
    private final static int METHOD_TIME_OUT = 10;

    /**
     * 配置切入点表达式
     */
    private static final String POINTCUT_EXPRESSION = "execution(* com.aicode..*.service..*.*(..))";

    /**
     * 事务管理器
     */
    @Resource
    private PlatformTransactionManager transactionManager;


    @Bean
    public TransactionInterceptor txAdvice() {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();

        RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
        readOnly.setReadOnly(true);
        readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);

        RuleBasedTransactionAttribute required = new RuleBasedTransactionAttribute();
        required.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        required.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        required.setTimeout(METHOD_TIME_OUT);

        Map<String, TransactionAttribute> attributesMap = new HashMap<>();
        attributesMap.put("save*", required);
        attributesMap.put("add*", required);
        attributesMap.put("insert*", required);
        attributesMap.put("create*", required);

        attributesMap.put("update*", required);
        attributesMap.put("batch*", required);
        attributesMap.put("modify*", required);
        attributesMap.put("edit*", required);

        attributesMap.put("remove*", required);
        attributesMap.put("delete*", required);
        attributesMap.put("clear*", required);

        attributesMap.put("select*", readOnly);
        attributesMap.put("get*", readOnly);
        attributesMap.put("query*", readOnly);
        attributesMap.put("list*", readOnly);
        attributesMap.put("count*", readOnly);
        attributesMap.put("find*", readOnly);
        attributesMap.put("load*", readOnly);
        attributesMap.put("search*", readOnly);
        attributesMap.put("fetch*", readOnly);

        source.setNameMap(attributesMap);
        return new TransactionInterceptor(transactionManager, source);
    }

    /**
     * 设置切面=切点pointcut+通知TxAdvice
     */
    @Bean
    public Advisor txAdviceAdvisor() {
        /* 声明切点的面：切面就是通知和切入点的结合。通知和切入点共同定义了关于切面的全部内容——它的功能、在何时和何地完成其功能*/
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        /*声明和设置需要拦截的方法,用切点语言描写*/
        pointcut.setExpression(POINTCUT_EXPRESSION);
        /*设置切面=切点pointcut+通知TxAdvice*/
        return new DefaultPointcutAdvisor(pointcut, txAdvice());
    }
}
