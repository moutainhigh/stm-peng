package com.mainsteam.stm.simple.manager.workbench.report.bo;

import java.io.Serializable;

/**
 * <li>文件名称: MessageInfo</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月22日 下午3:04:37
 * @author   俊峰
 */
public class MessageInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6452427582855822757L;
	private long userId;
	private long bizId;
	private String bizName;
	public MessageInfo(long userId, long bizId, String bizName) {
		super();
		this.userId = userId;
		this.bizId = bizId;
		this.bizName = bizName;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getBizId() {
		return bizId;
	}
	public void setBizId(long bizId) {
		this.bizId = bizId;
	}
	public String getBizName() {
		return bizName;
	}
	public void setBizName(String bizName) {
		this.bizName = bizName;
	}
	public MessageInfo() {
		super();
	}
	
	
}
