/*
 * Powered By [lixin]
 *
 */

package io.aicode.display.entity;
/**
 * 匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum DisplayAttributeMatchType implements java.io.Serializable {
    Equal("="), NotEqual("!="),Than(">"), ThanOrEqual(">="), LessOrEqual("<="),Less("<"),
    Like("like"), LefLike("左 like"), RightLike("右 like"),In("in"),
    Between("between"),
    ;
    public String val;

    DisplayAttributeMatchType(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static DisplayAttributeMatchType getEnum(String stateName) {

        for (DisplayAttributeMatchType projectStateEnum : DisplayAttributeMatchType.values()) {
            if (projectStateEnum.name().equalsIgnoreCase(stateName)) {
                return projectStateEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}

