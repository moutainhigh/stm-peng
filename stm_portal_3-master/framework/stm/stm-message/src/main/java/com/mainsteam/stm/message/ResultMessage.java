package com.mainsteam.stm.message;

import java.io.Serializable;

/**
 * <li>文件名称: ResultMessage</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 消息发送返回记录</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月6日 下午3:05:05
 * @author   zhangjunfeng
 */
public class ResultMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6956940727312281665L;
	private boolean success;
	private String message;
	
	
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ResultMessage(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	public ResultMessage(boolean success) {
		super();
		this.success = success;
	}
	public ResultMessage() {
		super();
	}
	
	
}
