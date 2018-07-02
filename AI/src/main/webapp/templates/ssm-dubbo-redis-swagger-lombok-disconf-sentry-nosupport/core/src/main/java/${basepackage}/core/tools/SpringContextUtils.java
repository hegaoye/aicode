package ${basePackage}.core.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * springBean获取工具
 * Created by shangze on 2017/6/15.
 */
public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext ctx;
    private static Logger logger = LoggerFactory.getLogger(SpringContextUtils.class);

    public SpringContextUtils() {
    }

    public static ApplicationContext getContext() {
        if(ctx == null) {
            logger.warn("SpringContextUtils通过Web容器获取ApplicationContext对象失败！即将通过XML配置文件来实例化容器！");
            ctx = new ClassPathXmlApplicationContext("framework/spring-core.xml");
        }

        return ctx;
    }

    public void setApplicationContext(ApplicationContext context) {
        if(ctx != null) {
            logger.warn("ApplicationContext被覆盖！");
        }

        ctx = context;
    }

    public void destroy() throws Exception {
        logger.debug("ApplicationContext被销毁！");
        ctx = null;
    }

    public static <T> T getBean(String name) {
        assertCtxIsNull();
        return (T) ctx.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        assertCtxIsNull();
        return ctx.getBean(requiredType);
    }

    private static void assertCtxIsNull() {
        if(ctx == null) {
            throw new IllegalStateException("SpringContextUtils的ApplicationContext为空，请检查问题！");
        }
    }
}
