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
import ${basePackage}.core.base.BaseDAO;
import ${basePackage}.core.base.BaseSVImpl;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;
import org.apache.ibatis.session.RowBounds;
import com.baidu.fsg.uid.UidGenerator;

@Service("${className?uncap_first}SV")
@Slf4j

public class ${className}2222222SVImpl extends BaseSVImpl<${className}, Long> implements ${className}SV {

    @Autowired
    private ${className}DAO ${classNameLower}DAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseDAO getBaseDAO() {
        return ${classNameLower}DAO;
    }

    /**
    * 保存account对象
    *
    * @param entity 实体
    * @throws BaseException
    */
    @Override
    public void save(${className} entity) throws BaseException {
        entity.setCode(String.valueOf(uidGenerator.getUID()));
        <#list fields as field>
        <#if field.checkPk && field.field?contains("code")>
        entity.set${field.field?cap_first}(String.valueOf(uidGenerator.getUID()));
        </#if>
        <#if field.checkDate>
        entity.set${field.field?cap_first}(new Date());
        </#if>
        </#list>
        super.save(entity);
    }

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
            Map<String,Object> param=new HashMap<>();
            <#list pkFields as pkField>
            param.put("${pkField.field}",${pkField.field});
            </#list>
            ${classNameLower}DAO.delete(param);
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
            if (offset < 0) {
               offset = 0;
            }

            if (limit < 0) {
               limit = Page.limit;
            }

            Map<String, Object> map = null;
            if (${classNameLower} != null) {
               map = JSON.parseObject(JSON.toJSONString(${classNameLower}, SerializerFeature.WriteDateUseDateFormat));
            } else {
               map = new HashMap<>();
            }
        return ${classNameLower}DAO.list(map, new RowBounds(offset, limit));
    }

    public  int count(${className} ${classNameLower}){
        Map<String, Object> map = null;
        if (${classNameLower} != null) {
           map = JSON.parseObject(JSON.toJSONString(${classNameLower}, SerializerFeature.WriteDateUseDateFormat));
        } else {
           map = new HashMap<>();
        }
       return ${classNameLower}DAO.count(map);
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
            Map<String,Object> param=new HashMap<>();
            <#list pkFields as pkField>
            param.put("${pkField.field}",${pkField.field});
            </#list>
            return ${classNameLower}DAO.list(param,new RowBounds(offset, limit));
    }

    @Override
    public int count(<#list pkFields as pkField>${pkField.fieldType} ${pkField.field}<#if pkField_has_next>,</#if></#list>) {
            Map<String,Object> param=new HashMap<>();
            <#list pkFields as pkField>
            param.put("${pkField.field}",${pkField.field});
            </#list>
            return ${classNameLower}DAO.count(param);
    }

}
