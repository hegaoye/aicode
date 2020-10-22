package com.aicode.config;
import com.aicode.setting.mq.FrameworksTemplateSink;
import com.aicode.setting.mq.ProjectJobLogsSink;
import com.aicode.setting.mq.DisplayAttributeSink;
import com.aicode.setting.mq.ProjectModuleSink;
import com.aicode.setting.mq.ModuleFileSink;
import com.aicode.setting.mq.MapFieldColumnSink;
import com.aicode.setting.mq.ProjectModelSink;
import com.aicode.setting.mq.AccountSink;
import com.aicode.setting.mq.ProjectModelClassSink;
import com.aicode.setting.mq.ProjectSink;
import com.aicode.setting.mq.MapClassTableSink;
import com.aicode.setting.mq.ProjectJobSink;
import com.aicode.setting.mq.ModuleSink;
import com.aicode.setting.mq.ProjectRepositoryAccountSink;
import com.aicode.setting.mq.FrameworksSink;
import com.aicode.setting.mq.ProjectMapSink;
import com.aicode.setting.mq.MapRelationshipSink;
import com.aicode.setting.mq.ProjectFramworkSink;
import com.aicode.setting.mq.ProjectCodeCatalogSink;
import com.aicode.setting.mq.ProjectSqlSink;
import com.aicode.setting.mq.SettingSink;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

/**
 * mq 注入 消息生产者消费者
 */
@Configuration
@EnableBinding({ FrameworksTemplateSink.class,ProjectJobLogsSink.class,DisplayAttributeSink.class,ProjectModuleSink.class,ModuleFileSink.class,MapFieldColumnSink.class,ProjectModelSink.class,AccountSink.class,ProjectModelClassSink.class,ProjectSink.class,MapClassTableSink.class,ProjectJobSink.class,ModuleSink.class,ProjectRepositoryAccountSink.class,FrameworksSink.class,ProjectMapSink.class,MapRelationshipSink.class,ProjectFramworkSink.class,ProjectCodeCatalogSink.class,ProjectSqlSink.class,SettingSink.class})
public class MqConfig {
}
