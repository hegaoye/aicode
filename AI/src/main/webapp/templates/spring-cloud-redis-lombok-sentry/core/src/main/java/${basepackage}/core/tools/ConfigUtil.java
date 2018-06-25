package ${basePackage}.core.tools;

import ${basePackage}.core.tools.security.SecurityUtil;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.*;

public class ConfigUtil extends PropertyPlaceholderConfigurer {

    private static final byte[] KEY = {9, -1, 0, 5, 39, 8, 6, 19};
    private static Map<String, String> ctxPropertiesMap;
    private List<String> decryptProperties;

    @Override
    protected void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
        ctxPropertiesMap = new HashMap<>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            if (decryptProperties != null && decryptProperties.contains(keyStr)) {
                value = SecurityUtil.decryptDes(value, KEY);
                props.setProperty(keyStr, value);
            }
            ctxPropertiesMap.put(keyStr, value);
        }
    }

    /**
     * @param decryptProperties the decryptPropertiesMap to set
     */
    public void setDecryptProperties(List<String> decryptProperties) {
        this.decryptProperties = decryptProperties;
    }

    /**
     * 获取配置文件值，如果不存在返回null
     * @param key
     * @return
     */
    public static String getValue(String key) {
        try {
            return ctxPropertiesMap.get(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }



    public static String getValue(String key, String config_file) {
        Properties p = new Properties();
        try {
            p.load(ConfigUtil.class.getClassLoader().getResourceAsStream(config_file));
            return p.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
