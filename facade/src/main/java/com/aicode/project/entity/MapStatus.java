/*
 * Powered By [lixin]
 *
 */

package com.aicode.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 类的状态机映射
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MapStatus implements java.io.Serializable {

    /**
     * 目标文件路径
     */
    private String targetFilePath;
    /**
     * 备注
     */
    private String notes;

    /**
     * 状态名
     */
    private String statusName;
    /**
     * 枚举名
     */
    private String name;

    /**
     * 枚举值
     */
    private String value;

    private List<MapStatus> mapStatusList;

}

