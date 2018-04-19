/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *       郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *       代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *       本代码仅用于AI-Code.
 */

package ${basePackage}.core.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于文件上传代理缓存业务处理
 * Created by lixin on 2017/6/3.
 */
@Data
public class UploadBasicEntity implements Serializable {

    private String uid;

    private String uniqueCode;

    private Object data;

    private List<String> paths;

    private String state;


    public enum UploadState {
        Init,/*初始态*/
        Changed,/*变更态*/
        Overdue;/*过期销毁*/
    }

    public UploadBasicEntity() {
    }

    public UploadBasicEntity(String uid, String uniqueCode) {
        this.uid = uid;
        this.uniqueCode = uniqueCode;
        this.state = UploadState.Init.name();
    }

    public UploadBasicEntity(String uid, String uniqueCode, Object data) {
        this.uid = uid;
        this.uniqueCode = uniqueCode;
        this.data = data;
        this.state = UploadState.Init.name();
    }

    public void setPaths(List<String> paths) {
        String path = paths.get(0).replace("//", "");
        path = path.substring(path.indexOf("/"));
        List<String> pathList = new ArrayList<>();
        pathList.add(path);
        this.paths = pathList;
    }

}
