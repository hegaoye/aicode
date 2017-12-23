/*
 * ${copyright}
 */

package ${package};

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

/**
 *${comment}
 *@author ${author}
 */
@Data
public class ${className} extends BaseEntity implements java.io.Serializable{

    <#list fields as field>
    private ${field.type} ${field.name};//数据库字段:${field.columnInfo.name}  属性显示:${field.columnInfo.notes}
    </#list>


}

