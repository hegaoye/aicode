/**
 * aicode
 */
package com.aicode.map.vo;


import com.aicode.display.vo.DisplayAttributeVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段属性映射信息 VO
 *
 * <p>相比实体 {@link com.aicode.map.entity.MapFieldColumn}，本 VO <b>刻意排除</b>两类成员：
 * <ul>
 *   <li><b>派生字段</b> {@code checkDate} / {@code checkState} / {@code checkPk} /
 *       {@code checkDigit} / {@code upper} —— 由 {@code isDate} / {@code isState} /
 *       {@code isPrimaryKey} / {@code fieldType} 推导而来，且实体上标了
 *       {@code @TableField(exist = false)}。放进响应只会让前端误以为是需要处理的数据。</li>
 *   <li><b>嵌套关联对象</b> {@code mapRelationship} —— 它内嵌 {@code MapClassTable}，
 *       而后者又持有 {@code List<MapFieldColumn>}，序列化会形成不受控的对象树。</li>
 * </ul>
 *
 * <p>本 VO 与 {@link DisplayAttributeVO} 共同构成「元驱动表单渲染所需的最小数据集」：
 * 前端据 {@code displayAttribute.displayType} 选择控件、据 {@code isRequired} 标必填、
 * 据 {@code matchType} 决定查询算子，单次请求即可完成渲染。
 *
 * @author aicode
 */
@Data
public class MapFieldColumnVO implements java.io.Serializable {
    @Schema(description = "数据库字段:id id")
    private Long id;
    @Schema(description = "数据库字段:mapClassTableCode 映射编码")
    private String mapClassTableCode;
    @Schema(description = "数据库字段:code 字段属性映射编码")
    private String code;
    @Schema(description = "数据库字段:column 字段")
    private String column;
    @Schema(description = "数据库字段:field 属性")
    private String field;
    @Schema(description = "数据库字段:sqlType 字段类型")
    private String sqlType;
    @Schema(description = "数据库字段:fieldType 属性类型")
    private String fieldType;
    @Schema(description = "数据库字段:notes 注释")
    private String notes;
    @Schema(description = "数据库字段:defaultValue 字段默认值")
    private String defaultValue;
    @Schema(description = "数据库字段:isPrimaryKey 是否是主键")
    private String isPrimaryKey;
    @Schema(description = "数据库字段:isDate 是否是时间类型")
    private String isDate;
    @Schema(description = "数据库字段:isState 是否是状态")
    private String isState;

    /**
     * 该字段的表单展示属性（控件类型 / 必填 / 匹配方式 / 校验提示 / 显示序 …）。
     * 为 {@code null} 表示尚未配置，前端 SHALL 回退到默认控件。
     */
    @Schema(description = "表单展示属性：控件类型、必填、查询匹配方式、校验提示、显示顺序等；未配置时为 null")
    private DisplayAttributeVO displayAttribute;
}
