package ${basePackage}.core.tools.memcached;


import ${basePackage}.core.tools.ConfigUtil;
import ${basePackage}.core.tools.DateTools;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

public class MemCachedUtil {
    private final static Logger logger = LoggerFactory.getLogger(MemCachedUtil.class);

    // 创建MemCachedClient全局对象
    private static MemCachedClient mcc = new MemCachedClient();
    // 服务器列表和其权重
    private static String[] servers;
    private static Integer[] weights = {2};


    /**
     * 定义固定常用时间
     */
    public static enum TimeOut {
        tenSeconds("10秒 10s*1S", "10s"),
        oneMinutes("1分钟 60s*1M", "1m"),
        threeMinutes("3分钟 60s*3M", "3m"),
        fiveMinutes("5分钟 60s*5M", "5m"),
        tenMinutes("10分钟 60s*10M", "10m"),
        fifteenMinutes("15分钟 60s*15M", "10m"),
        halfOfHours("半小时 3600s*1/2h", "30m"),
        oneHours("一小时 3600s*1h ", "1h"),
        twelveHours("12小时 3600s*12h", "12h"),
        oneDay("一天 3600s*24h", "1d"),
        threeDay("三天 3600s*24h*3day", "3d"),
        oneWeek("7天 3600s*24h*7day", "7d"),
        halfOfMonth("半月 3600s*24h*15day", "15d"),
        oneMonth("1个月 3600s*24h*30day", "1M");
        // 成员变量
        private String name;
        private String value;

        // 构造方法
        private TimeOut(String name, String value) {
            this.name = name;
            this.value = value;
        }

        // 普通方法
        public static String getName(String index) {
            for (TimeOut s : TimeOut.values()) {
                if (s.getName().equalsIgnoreCase(index)) {
                    return s.name;
                }
            }
            return null;
        }

        // get set 方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static {
        init();
        // 创建Socket连接池对象
        SockIOPool pool = SockIOPool.getInstance();
        // 设置服务器信息
        pool.setServers(servers);
        pool.setWeights(weights);
        pool.setFailover(true);

        // 设置初始连接数、最小和最大连接数以及最大处理时间
        pool.setInitConn(5);
        pool.setMinConn(5);
        pool.setMaxConn(250);
        pool.setMaxIdle(1000 * 60 * 60 * 6);

        // 设置主线程睡眠时间
        pool.setMaintSleep(30);

        // 设置TCP参数、连接超时等
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setSocketConnectTO(0);
        pool.setAliveCheck(true);

        // 初始化连接池
        pool.initialize();
    }

    /**
     * @return : void
     * @Enclosing_Method : init
     * @Written by        : lixin
     * @Creation Date     : 2012-2-29 上午09:57:09
     * @version : v1.00
     * @Description : 初始化
     */
    private static void init() {
        String server = ConfigUtil.getValue("cache_servers");
        String weight = ConfigUtil.getValue("cacge_weights");
        logger.info("===> 初始化 Memache");
        logger.info("===> memcache 服务器地址: " + server);
        //初始化服务器地址
        servers = server.split(",");
        String[] aryWeight = weight.split(",");
        for (int i = 0; i < aryWeight.length; i++) {
            weights[i] = Integer.parseInt(aryWeight[i]);
        }
    }

    // 受保护的对象
    protected static MemCachedUtil instance = null;

    public static MemCachedUtil getInstance() {
        if (instance == null) {
            instance = new MemCachedUtil();
        }
        return instance;
    }

    protected MemCachedUtil() {

    }

    /**
     * 设置自定义内容到memcache中缓存使用key为键，time为有效时间以秒为单位，val为缓存内容
     *
     * @param key  键
     * @param obj  数据对象
     * @param time 时间 例如 1s 1秒，30m 分钟 ，12h 小时， 15d 15天 ，1M 一个月，1y 一年
     */
    public Boolean setCache(String key, Object obj, TimeOut time) {
        Boolean isSet = false;
        //isSet = mcc.set(key, obj, DateTools.getSomeOneDay(DateTools.dateToNum14(new Date()), time.getValue()));//预期的过期时间，适合linux
        isSet = mcc.set(key, obj, new Date(DateTools.getMilliseconds(time.getValue())));//预期的过期毫秒数，适合windows
        return isSet;
    }

    /**
     * 添加缓存，如有还没有key则返回true,已经有key则返回false
     *
     * @param key
     * @param obj
     * @param time 时间 例如 1s 1秒，30m 分钟 ，12h 小时， 15d 15天 ，1M 一个月，1y 一年
     */
    public Boolean addCache(String key, Object obj, TimeOut time) {
        Boolean isAdd = false;
        isAdd = mcc.add(key, obj, DateTools.getSomeOneDay(DateTools.dateToNum14(new Date()), time.getValue()));
        return isAdd;
    }

    /**
     * 取出指定键值
     *
     * @param key
     */
    public Map<String, Object> getCache(String key) {
        Map<String, Object> map = null;
        map = (Map<String, Object>) mcc.get(key);
        return map;
    }

    /**
     * 获得str类型的val
     *
     * @param key
     * @return string类型
     */
    public String getVal(String key) {
        String val = null;
        val = (String) mcc.get(key);
        return val;
    }

    /**
     * 获得str类型的val
     *
     * @param key
     * @return string类型
     */
    public Object getObj(String key) {
        Object val = null;
        val = mcc.get(key);
        return val;
    }

    /**
     * 删除指定键值
     *
     * @param key
     */
    public Boolean deleteCache(String key) {
        Boolean isDel = false;
        isDel = mcc.delete(key);
        return isDel;
    }

    /**
     * 根据键修改值
     *
     * @param key
     * @param obj
     * @param time 时间 例如 1s 1秒，30m 分钟 ，12h 小时， 15d 15天 ，1M 一个月，1y 一年
     * @return
     */
    public Boolean replaceCache(String key, Object obj, TimeOut time) {
        Boolean isDel = false;
        isDel = mcc.replace(key, obj, DateTools.getSomeOneDay(DateTools.dateToNum14(new Date()), time.getValue()));
        return isDel;
    }

    public boolean add(String key, Object value) {
        return mcc.add(key, value);
    }

    public boolean add(String key, Object value, Date expiry) {
        return mcc.add(key, value, expiry);
    }

    public boolean replace(String key, Object value) {
        return mcc.replace(key, value);
    }

    public boolean replace(String key, Object value, Date expiry) {
        return mcc.replace(key, value, expiry);
    }


    public Object get(String key) {
        return mcc.get(key);
    }
}