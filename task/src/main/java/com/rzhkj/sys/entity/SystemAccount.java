/*
 *  Copyright (c) 2017. 郑州仁中和科技有限公司.保留所有权利.
 *                        http://www.rzhkj.com/
 *        郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.
 *        代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.
 *        本代码仅用于龐帝業務系统.
 *
 */

/*
 * Powered By [lixin]
 * 代码脚手架工具生成 [rapid-framework]
 */

package com.rzhkj.sys.entity;

import com.rzhkj.core.base.BaseEntity;
import lombok.Data;

/**
 * @author lixin hegaoye@qq.com
 * @version 1.0
 * @since 1.0
 */
@Data
public class SystemAccount extends BaseEntity implements java.io.Serializable {
    private static final long serialVersionUID = 5454155825314635342L;

    //可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
    private Long id;//数据库字段:id  属性显示:id
    private String accountCode;//数据库字段:accountCode  属性显示:账户编码
    private String account;//数据库字段:account  属性显示:账户,，默认邮箱
    private String password;//数据库字段:password  属性显示:密码
    private String state;//数据库字段:state  属性显示:状态：激活[Activate]，冻结[Frozen]，删除[Delete]
    private String accountLevel;//数据库字段:accountLevel  属性显示:账户级别:super, gold,silver,copper
    private java.util.Date createTime;//数据库字段:createTime  属性显示:创建时间,utc
    private java.util.Date updateTime;//数据库字段:updateTime  属性显示:更新时间,utc

    public SystemAccount() {
    }

    public SystemAccount(long id, String account, String accountLevel, String state) {
        this.id = id;
        this.account = account;
        this.accountLevel = accountLevel;
        this.state = state;
    }

    //状态：激活[Activate]，冻结[Frozen]，删除[Delete]
    public enum State {
        Activate("激活"),
        Frozen("冻结"),
        Delete("删除");
        public String val;

        State(String val) {
            this.val = val;
        }


        /**
         * 根据状态名称查询状态
         *
         * @param stateName
         * @return
         */
        public static State getState(String stateName) {
            for (State state : State.values()) {
                if (state.name().equalsIgnoreCase(stateName)) {
                    return state;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return this.name();
        }
    }
}

