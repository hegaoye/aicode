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

package com.rzhkj.sys.vo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

public class SystemAccountQuery  implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;

	/** 账户编码 */
	private String accountCode;
	/** 账户,，默认邮箱 */
	private String account;
	/** 密码 */
	private String password;
	/** 状态：激活[Activate]，冻结[Frozen]，删除[Delete] */
	private String state;
	/** 账户级别:super, gold,silver,copper */
	private String accountLevel;
	/** 创建时间,utc */
	private java.util.Date createTimeBegin;
	private java.util.Date createTimeEnd;
	private java.util.Date createTime;
	/** 更新时间,utc */
	private java.util.Date updateTimeBegin;
	private java.util.Date updateTimeEnd;
	private java.util.Date updateTime;


	public String getAccountCode() {
		return this.accountCode;
	}

	public void setAccountCode(String value) {
		this.accountCode = value;
	}

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String value) {
		this.account = value;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String value) {
		this.password = value;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String value) {
		this.state = value;
	}

	public String getAccountLevel() {
		return this.accountLevel;
	}

	public void setAccountLevel(String value) {
		this.accountLevel = value;
	}

	public java.util.Date getCreateTimeBegin() {
		return this.createTimeBegin;
	}

	public void setCreateTimeBegin(java.util.Date value) {
		this.createTimeBegin = value;
	}

	public java.util.Date getCreateTimeEnd() {
		return this.createTimeEnd;
	}

	public void setCreateTimeEnd(java.util.Date value) {
		this.createTimeEnd = value;
	}

	public java.util.Date getUpdateTimeBegin() {
		return this.updateTimeBegin;
	}

	public void setUpdateTimeBegin(java.util.Date value) {
		this.updateTimeBegin = value;
	}

	public java.util.Date getUpdateTimeEnd() {
		return this.updateTimeEnd;
	}

	public void setUpdateTimeEnd(java.util.Date value) {
		this.updateTimeEnd = value;
	}


	public String toString() {
		return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
	}

}

