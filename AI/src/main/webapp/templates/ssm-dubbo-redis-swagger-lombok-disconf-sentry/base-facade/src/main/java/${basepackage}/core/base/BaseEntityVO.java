package ${basePackage}.core.base;


import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

/**
 * @author shangze
 */
public class BaseEntityVO implements java.io.Serializable {

    /**
     * 把 Object2 中的值设置到 Object1 中
     *
     * @param targetObject
     * @param Object2
     * @param calsz
     * @return
     */
    public static Object mergeObject(Object targetObject, Object Object2, Class calsz) {
        Map targetPara = (Map) JSON.toJSON(targetObject);
        Map oldPara = (Map) JSON.toJSON(Object2);
        for (Object key : targetPara.keySet()) {
            if (targetPara.get(key) == null || targetPara.get(key).toString().length() < 1) {
                if (oldPara.get(key) != null && oldPara.get(key).toString().length() > 0) {
                    targetPara.put(key, oldPara.get(key));
                }
            }
        }
        return JSON.parseObject(JSON.toJSONString(targetPara), calsz);
    }

    /**
     * 将对像转化为需要的类型
     *
     * @param obj   需要转化对象
     * @param clazz 转化结果
     * @return
     */
    public static <T> T toVo(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        String json = JSON.toJSONString(obj);
        return JSON.parseObject(json, clazz);
    }


    /**
     * 将对像转化为需要的类型
     *
     * @param obj   需要转化对象
     * @param clazz 转化结果
     * @return
     */

    public static <T> T toEntity(Object obj, Class<T> clazz) {
        return toVo(obj, clazz);
    }


    /**
     * 将集合转化为需要的类型
     *
     * @param obj   需要转化对象
     * @param clazz 转化结果
     * @return
     */
    public static <T> List<T> convertList(Object obj, Class<T> clazz) {
        String json = JSON.toJSONString(obj);
        return JSON.parseArray(json, clazz);
    }

}
