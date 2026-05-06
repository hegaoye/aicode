package com.aicode.core;


import lombok.Data;

import java.util.Date;

@Data
public class BaseVO implements java.io.Serializable {

    /**
     * 创建时间 yyyy-MM-dd HH:mm:ss
     */
    protected Date createTimeBegin;
    protected Date createTimeEnd;
    /**
     * 更新时间 yyy-MM-dd HH:mm:ss
     */
    protected Date updateTimeBegin;
    protected Date updateTimeEnd;

}
