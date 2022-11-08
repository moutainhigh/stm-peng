package com.mainsteam.stm.simple.manager.workbench.report.bo;

import java.io.Serializable;

/**
 * <li>文件名称: MsgUserBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月17日
 * @author   ziwenwen
 */
public class MsgUserBo implements Serializable{
	
	private static final long serialVersionUID = -6924763498710628512L;

	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 用户账户
	 */
	private String account;
	
	/**
	 * 用户姓名
	 */
	private String name;
	
	/**
	 * 负责人描述信息
	 * 形如：考勤系统负责人
	 */
	private String description;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}


