/*
 * demo
 */
package com.aicode.display.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 显示属性 的实体类
 *
 * @author aicode
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DisplayAttribute implements java.io.Serializable {
    @Schema(description = "数据库字段:id ")
    private Long id;
    @Schema(description = "数据库字段:mapClassTableCode 类模型编码")
    @TableField("mapClassTableCode")
    private String mapClassTableCode;
    @Schema(description = "数据库字段:mapFieldColumnCode 字段编码")
    @TableField("mapFieldColumnCode")
    private String mapFieldColumnCode;
    @Schema(description = "数据库字段:isRequired 是否必填 Y,N")
    @TableField("isRequired")
    private String isRequired;
    @Schema(description = "数据库字段:isInsert 是否插入")
    @TableField("isInsert")
    private String isInsert;
    @Schema(description = "数据库字段:isDeleteCondition 是否是删除条件")
    @TableField("isDeleteCondition")
    private String isDeleteCondition;
    @Schema(description = "数据库字段:isAllowUpdate 是否允许修改 Y,N")
    @TableField("isAllowUpdate")
    private String isAllowUpdate;
    @Schema(description = "数据库字段:isListPageDisplay 是否分页列表显示 Y,N")
    @TableField("isListPageDisplay")
    private String isListPageDisplay;
    @Schema(description = "数据库字段:isDetailPageDisplay 是否详情页显示 Y,N")
    @TableField("isDetailPageDisplay")
    private String isDetailPageDisplay;
    @Schema(description = "数据库字段:isQueryRequired 是否是查询条件 Y,N")
    @TableField("isQueryRequired")
    private String isQueryRequired;
    @Schema(description = "数据库字段:isLineNew 是否换行")
    @TableField("isLineNew")
    private String isLineNew;
    @Schema(description = "数据库字段:matchType 匹配方式（查询表单用）。"
            + "值域取自下方 allowableValues —— 该字段无对应枚举类，"
            + "故只收录能从既有 description 明确读出的值，不编造",
            allowableValues = {"=", "!=", ">=", "<=", ">", "<", "like", "between", "in"})
    @TableField("matchType")
    private String matchType;
    @Schema(description = "数据库字段:displayType 控件类型。"
            + "取值见 allowableValues（DisplayAttributeDisplayType 的全部 24 个枚举，按字母序）；"
            + "值域进入契约 enum 是为了让前端能据此生成下拉并做类型校验，"
            + "而不是在前端另写一份会静默过期的字符串副本。"
            + "【维护须知】Java 注解属性只接受编译期常量 —— 无法写成对 values() 的引用，"
            + "static final String[] 也不行（数组引用不是常量表达式），故此处必须手写字面量。"
            + "本列表与枚举类的一致性由 ApiContractIT 场景⑬ 的「enum 集合 SHALL 与枚举类完全一致」"
            + "断言守护：新增枚举而漏改此处，该用例立刻红",
            allowableValues = {
                    "Autocomplete", "Cascader", "Checkbox", "DatePicker", "Email", "IdCard",
                    "Input", "InputNumber", "Mention", "Mobile", "MobileOrPhone", "MultiSelect",
                    "Password", "Phone", "Radio", "Rate", "Select", "Silder", "Switch",
                    "Textarea", "TimePicker", "TreeSelect", "Upload", "Website"})
    @TableField("displayType")
    private String displayType;
    @Schema(description = "数据库字段:displayName 显示列名称")
    @TableField("displayName")
    private String displayName;
    @Schema(description = "数据库字段:displayNo 显示顺序")
    @TableField("displayNo")
    private Integer displayNo;
    @Schema(description = "数据库字段:fieldValidationMode 字段校验方式。"
            + "值域取自下方 allowableValues —— 该字段无对应枚举类，"
            + "故只收录能从既有 description 明确读出的值，不编造",
            allowableValues = {"Email", "Address", "Telephone", "Password", "Date", "Number",
                    "Integer", "Positive_Integer", "text", "IdCard", "Website"})
    @TableField("fieldValidationMode")
    private String fieldValidationMode;
    @Schema(description = "数据库字段:validateText 验证提示语")
    @TableField("validateText")
    private String validateText;
    @Schema(description = "数据库字段:displayCss 显示css样式")
    @TableField("displayCss")
    private String displayCss;

}
