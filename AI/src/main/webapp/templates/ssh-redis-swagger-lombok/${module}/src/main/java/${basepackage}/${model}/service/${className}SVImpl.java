/*
 * ${copyright}
 */
package ${basePackage}.${model}.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.alibaba.fastjson.JSON;
import ${basePackage}.core.base.BaseServiceImpl;
import ${basePackage}.core.base.Page;
import ${basePackage}.${model}.facade.${className}SV;
import ${basePackage}.${model}.entity.${className};

/**
 * ${notes}
 * @author ${author}
 */
@Component
@Service
public class ${className}SVImpl extends BaseServiceImpl<${className}> implements ${className}SV{

}
