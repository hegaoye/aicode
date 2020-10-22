package com.aicode.core.tools;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mm
 */
public enum BeanTools {
    Instance;

    /**
     * 集合拷贝
     *
     * @param sourceList 源集合
     * @param clazz      目标类
     * @return 目标类集合
     */
    public <T> List<T> copyProperties(List<?> sourceList, Class<T> clazz) {
        List<T> tList = new ArrayList<>();
        for (Object o : sourceList) {
            try {
                T target = clazz.newInstance();
                BeanUtils.copyProperties(o, target);
                tList.add(target);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

        return tList;
    }


}
