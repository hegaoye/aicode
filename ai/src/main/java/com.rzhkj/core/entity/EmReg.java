package com.rzhkj.core.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 枚举注册表，系统中所有的静态数据枚举和实体枚举都会主动注册到此，主要用于枚举获取Map<String,String>
 * 原则上，Std类中，以及所有实体类中的枚举都会在这里注册出现
 *
 * @author yangxiao 2017-08-29 16:38
 */
public class EmReg implements Serializable {

    /**
     * key为枚举类名（不含包名），val为枚举的Map
     */
    private static final Map<String, Map<String, String>> mmReg = new HashMap<String, Map<String, String>>();
    /**
     * 类注册表
     */
    private static final Set<Class> classSet = new HashSet<Class>();

    private static void init() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        String perfix;
        for (Class clazz : classSet) {
            perfix = clazz.getSimpleName() + ".";   //声明前缀
            for (Class aClass : clazz.getClasses()) {
                if (aClass.isEnum()) {
                    Method method = aClass.getDeclaredMethod("toMap", String.class);
                    method.invoke(clazz.newInstance(), perfix + aClass.getSimpleName());
                }
            }
        }
    }

    public void outMap() {

    }


    /**
     * 根据类名（不含包名）获取枚举的Map形式，主要用于前端展示用，内部类中的“.”号可用“_”代替
     *
     * @param emName 类名，比如Ord.Optype，或者Ord_St
     * @return 枚举的Map形式
     */
    public static Map<String, String> get(String emName) {
        if (emName == null) return null;
        emName = emName.replaceAll("_", ".");
        return mmReg.get(emName);
    }

    public static void put(String emName, Map<String, String> map) {
        mmReg.put(emName, map);
    }
}
