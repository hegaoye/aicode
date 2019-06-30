/*
 *
 *                        http://www.aicode.io
 *
 *
 *       本代码仅用于AI-Code.
 */

package io.aicode.base.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于文件上传代理缓存业务处理
 * Created by lixin on 2017/6/3.
 */
public class UploadBasicEntity implements Serializable {

    @Getter
    @Setter
    private String uid;

    @Getter
    @Setter
    private String uniqueCode;

    @Getter
    @Setter
    private Object data;

    @Getter
    private List<String> paths;

    @Getter
    @Setter
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
