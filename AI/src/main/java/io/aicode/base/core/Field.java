package io.aicode.base.core;

import io.aicode.base.core.typemapping.DatabaseDataTypesUtils;
import io.aicode.base.enums.YNEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class Field implements Serializable {
    private Long id;//数据库字段:id  属性显示:id
    private String mapClassTableCode;//数据库字段:mapClassTableCode  属性显示:映射编码
    private String code;//数据库字段:code  属性显示:字段编码
    private String column;//数据库字段:column  属性显示:字段
    private String field;//数据库字段:field  属性显示:属性
    private String sqlType;//数据库字段:sqlType  属性显示:字段类型
    private String fieldType;//数据库字段:fieldType  属性显示:属性类型
    private String notes;//数据库字段:notes  属性显示:注释
    private String defaultValue;//数据库字段:defaultValue  属性显示:字段默认值
    private String isPrimaryKey;//数据库字段:isPrimaryKey  属性显示:是否是主键
    private String isDate;//数据库字段:isDate  属性显示:是否是时间类型
    private String isState;//数据库字段:isStateOrType  属性显示:是否是状态


    /**
     * 数据库字段:isRequired  属性显示:是否必填 Y,N
     */
    private boolean isRequired;
    /**
     * 数据库字段:isInsert  属性显示:是否插入
     */
    private boolean isInsert;
    /**
     * 数据库字段:isDeleteCondition  属性显示:是否是删除条件
     */
    private boolean isDeleteCondition;
    /**
     * 数据库字段:isAllowUpdate  属性显示:是否允许修改 Y,N
     */
    private boolean isAllowUpdate;
    /**
     * 数据库字段:isListPageDisplay  属性显示:是否分页列表显示 Y,N
     */
    private boolean isListPageDisplay;
    /**
     * 数据库字段:isDetailPageDisplay  属性显示:是否详情页显示 Y,N
     */
    private boolean isDetailPageDisplay;
    /**
     * 数据库字段:isQueryRequired  属性显示:是否是查询条件 Y,N
     */
    private boolean isQueryRequired;
    /**
     * 数据库字段:isLineNew  属性显示:是否换行
     */
    private boolean isLineNew;
    /**
     * 数据库字段:matchType  属性显示:匹配方式 =,!=,>=,<=,>,<,like,左like，右like,between,in
     */
    private String matchType;
    /**
     * 数据库字段:displayType  属性显示:显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar
     */
    private String displayType;
    /**
     * 数据库字段:displayName  属性显示:显示列名称
     */
    private String displayName;
    /**
     * 数据库字段:displayNo  属性显示:显示顺序
     */
    private Integer displayNo;
    /**
     * 数据库字段:fieldValidationMode  属性显示:字段验证方式
     */
    private String fieldValidationMode;
    /**
     * 数据库字段:validateText  属性显示:验证提示语
     */
    private String validateText;
    /**
     * 数据库字段:displayCss  属性显示:显示css样式
     */
    private String displayCss;

    private boolean checkDate;
    private boolean checkState;
    private boolean checkPk;
    private boolean checkDigit = false;
    private String upper;

    public void toJava() {
        this.field = StringHelper.toJavaVariableName(this.column);
        if (this.field.equals("id")) {
            this.fieldType = "java.lang.Long";
        } else {
            this.fieldType = DatabaseDataTypesUtils.getPreferredJavaType(this.sqlType);
        }
    }

    public boolean getCheckDate() {
        return isDate.equals(YNEnum.Y.name()) ? true : false;
    }

    public boolean getCheckState() {
        return isState.equals(YNEnum.Y.name()) ? true : false;
    }

    public boolean getCheckPk() {
        return isPrimaryKey.equals(YNEnum.Y.name()) ? true : false;
    }

    public boolean getCheckDigit() {
        this.checkDigit = DatabaseDataTypesUtils.isIntegerNumber(this.fieldType);
        if (!this.checkDigit) {
            this.checkDigit = DatabaseDataTypesUtils.isFloatNumber(this.fieldType);
        }
        return checkDigit;
    }

    public String getUpper() {
        this.upper = StringHelper.capitalize(field);
        return this.upper;
    }

}
