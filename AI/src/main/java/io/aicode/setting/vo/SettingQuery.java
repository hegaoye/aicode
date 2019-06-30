/*
 *
 *                        http://www.aicode.io
 *
 *
 *        本代码仅用于AI-Code.
 *
 */


package io.aicode.setting.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SettingQuery implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;

    /**
     * 键
     */
    private String k;
    /**
     * 值
     */
    private String v;

    /**
     * 说明
     */
    private String summary;

}

