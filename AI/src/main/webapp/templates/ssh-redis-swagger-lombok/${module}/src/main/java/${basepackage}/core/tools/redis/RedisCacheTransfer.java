package ${basePackage}.core.tools.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * cache静态注入中间类
 * Created by shangze on 2017/6/15.
 */
public class RedisCacheTransfer {

    @Autowired
    public void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
//        MybatisRedisCache.setJedisConnectionFactory(jedisConnectionFactory);
    }

}
