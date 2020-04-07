/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

import lombok.Data;


/**
 * 类的状态机映射
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@Data
public class MapState implements java.io.Serializable {

    private String state;
    private String value;

}

