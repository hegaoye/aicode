/**
 * AI-Code 为您构建代码，享受智慧生活!
 */
package com.aicode.setting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设置 VO
 *
 * @author hegaoye
 */
@Data
public class SettingVO implements java.io.Serializable {

    /**
     * 数据库字段:id  属性显示:id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 数据库字段:k  属性显示:键
     */
    @Schema(description = "键")
    private String k;
    /**
     * 数据库字段:v  属性显示:值
     */
    @Schema(description = "值")
    private String v;
    /**
     * 数据库字段:description  属性显示:说明
     */
    @Schema(description = "说明")
    private String description;

}
