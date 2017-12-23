/*
 *${copyright}
 */

package ${package};

import com.baidu.fsg.uid.UidGenerator;
import com.rzhkj.core.base.BaseMybatisDAO;
import com.rzhkj.core.base.BaseMybatisSVImpl;
import com.rzhkj.project.dao.ProjectBuildToolsDAO;
import com.rzhkj.project.entity.ProjectBuildTools;
import com.rzhkj.project.service.ProjectBuildToolsSV;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *${comment}接口
 *@author ${author}
 */
@Component
@Service
public class ${className} extends BaseMybatisSVImpl<${classSimpleName}, Long> implements ${classSimpleName}SV {

    @Resource
    private ${classSimpleName}DAO ${classSimpleNameLower}DAO;

    @Resource
    private UidGenerator uidGenerator;

    @Override
    protected BaseMybatisDAO getBaseMybatisDAO() {
        return ${classSimpleNameLower}DAO;
    }

}
