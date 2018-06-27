/*
 * ${copyright}
 */
package ${basePackage}.${model}.service;

import ${basePackage}.core.entity.Page;
import ${basePackage}.core.exceptions.BaseException;
import ${basePackage}.core.exceptions.${className}Exception;
import ${basePackage}.${model}.dao.${className}DAO;
import ${basePackage}.${model}.entity.${className}State;
import ${basePackage}.${model}.entity.${className};
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.RowBounds;
import com.baidu.fsg.uid.UidGenerator;

@Service("${classNameLower}SV")
@Slf4j
public class ${className}SVImpl implements ${className}SV {

    @Autowired
    private ${className}DAO ${classNameLower}DAO;

    @Resource
    private UidGenerator uidGenerator;

<#if (pkFields?size>0)>
    /**
     * 加载一个对象${className}
     <#list pkFields as field>* @param ${field.field} ${field.notes}</#list>
     * @return ${className}
     */
    @Override
    public ${className} load(<#list pkFields as field>${field.fieldType} ${field.field}<#if field_has_next>,</#if></#list>) {
           if(<#list pkFields as field>${field.field}==null<#if field_has_next>&&</#if></#list>){
                 throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
           }

            Map<String,Object> param=new HashMap<>();
            <#list pkFields as field>
            param.put("${field.field}",${field.field});
            </#list>
            return ${classNameLower}DAO.load(param);
    }
    <#list pkFields as pkField>
    /**
     * 加载一个对象${className} 通过${pkField.field}
     * @param ${pkField.field} ${pkField.notes}
     * @return ${className}
     */
    @Override
    public ${className} loadBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field}) {
            if(${pkField.field}==null){
                 throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
            }
            return ${classNameLower}DAO.loadBy${pkField.field?cap_first}(${pkField.field});
    }

    </#list>

    <#list pkFields as pkField>
    /**
    * 根据主键${pkField.field},oldStates 共同更新 ${className} 的状态到newState状态
    *
    * @param ${pkField.field} ${pkField.notes}
    * @param newState 新状态
    * @param oldStates 旧状态集合
    */
    public void updateStateBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field},${className}State newState,${className}State... oldStates){
        if(${pkField.field}==null){
            throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
        }
        if(newState==null){
            throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
        }
        if(oldStates==null){
            throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
        }
        ${classNameLower}DAO.updateStateBy${pkField.field?cap_first}(${pkField.field},newState,oldStates);
    }
    </#list>

    <#list pkFields as pkField>
    /**
    * 根据主键${pkField.field} 更新 ${className} 的状态到另一个状态
    *
    * @param ${pkField.field} ${pkField.notes}
    * @param state 状态
    */
    public void updateBy${pkField.field?cap_first}(${pkField.fieldType} ${pkField.field},${className}State state){
            if(${pkField.field}==null){
               throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
            }
          ${classNameLower}DAO.updateBy${pkField.field?cap_first}(${pkField.field},state);
    }
    </#list>


    /**
     * 删除对象${className}
     <#list pkFields as pkField>* @param ${pkField.field} ${pkField.notes}</#list>
     * @return ${className}
     */
    @Override
    public void delete(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
            if(<#list pkFields as field>${field.field}==null<#if field_has_next>&&</#if></#list>){
                throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);
            }
            ${classNameLower}DAO.delete(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
    }

</#if>


    /**
    * 查询${className}分页
    *
    * @param ${classNameLower}  对象
    * @param offset 查询开始行
    * @param limit  查询行数
    * @return List<${className}>
    */
    @Override
    public List<${className}> list(${className} ${classNameLower}, int offset, int limit) {
        return ${classNameLower}DAO.list(${classNameLower}, new RowBounds(offset, limit));
    }

    /**
     * 查询${className}分页
     *
     <#list pkFields as pkField>* @param ${pkField.field}  ${pkField.notes}</#list>
     * @param offset 查询开始行
     * @param limit  查询行数
     * @return List<${className}>
     */
    @Override
    public List<${className}> list(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field},</#list>int offset, int limit) {
            if (offset < 0) {
              offset = 0;
            }

            if (limit < 0) {
              limit = Page.limit;
            }

            return ${classNameLower}DAO.list(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>,new RowBounds(offset, limit));
    }

    @Override
    public int count(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
            return ${classNameLower}DAO.count(<#list pkFields as pkField>${pkField.field}<#if pkField_has_next>,</#if></#list>);
    }


    @Override
    public int count(${className} ${classNameLower}) {
        return ${classNameLower}DAO.count(${classNameLower});
    }

    @Override
    public int count() {
        return ${classNameLower}DAO.count();
    }

    /**
    * 保存
    *
    * @param ${classNameLower} 实体
    * @throws BaseException
    */
    @Override
    public void save(${className} ${classNameLower}) {
        if(${classNameLower}==null){ throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);}
        ${classNameLower}DAO.insert(${classNameLower});
    }

    /**
    * 更新
    *
    * @param ${classNameLower} 实体
    * @throws BaseException
    */
    @Override
    public void modify(${className} ${classNameLower}) {
        if(${classNameLower}==null){ throw new ${className}Exception(BaseException.BaseExceptionEnum.Ilegal_Param);}
        ${classNameLower}DAO.update(${classNameLower});
    }

}
