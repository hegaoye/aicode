/*
 * Powered By [lixin]
 *
 */

package io.aicode.project.entity;

/**
 * 显示属性：显示格式 自动完成 Autocomplete,级联选择 Cascader,日期选择框 DatePicker,时间选择 TimePicker,输入框 Input,数字输入框 InputNumber,
 * 提及 Mention,邮箱 Email，电话Phone，手机Mobile，备注说明 Summary，选择器 Select，单选 Radio，多选框 Checkbox,评分 Rate,加载展位图 Skeleton,滑动输入条 Silder，
 * 开关 Switch,穿梭框 Transfer,选择树 TreeSelect ,上传 Upload,头像 Avatar',
 *
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
public enum DisplayAttributeEnum implements java.io.Serializable {
    Autocomplete("自动完成"), Cascader("级联选择"), DatePicker("日期选择框"), TimePicker("时间选择"),
    Input("输入框"), InputNumber("数字输入框"), Mention("提及"), Email("邮箱"), Phone("电话"),
    Mobile("手机"), Summary("备注说明"), Select("选择器"), Radio("单选"), Checkbox("多选框"),
    Rate("评分"), Skeleton("加载展位图"), Silder("滑动输入条"), Switch("开关"), Transfer("穿梭框"),
    TreeSelect("选择树"), Upload("上传"), Avatar("头像"),
    ;
    public String val;

    DisplayAttributeEnum(String val) {
        this.val = val;
    }

    /**
     * 根据状态名称查询状态
     *
     * @param stateName
     * @return
     */
    public static DisplayAttributeEnum getEnum(String stateName) {
        for (DisplayAttributeEnum projectStateEnum : DisplayAttributeEnum.values()) {
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

