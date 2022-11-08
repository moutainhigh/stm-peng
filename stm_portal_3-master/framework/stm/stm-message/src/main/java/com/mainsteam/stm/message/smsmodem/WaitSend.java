package com.mainsteam.stm.message.smsmodem;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>文件名称: WaitSend</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 待发送短信</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日 下午6:48:03
 * @author   俊峰
 */
public class WaitSend implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1992009738854226148L;
	/**
	 * 待发送短信任务ID
	 */
	private long taskId;
	/**
	 * 短信接收人号码，多个号码以“;”分隔
	 */
	private String destNumber;
	/**
	 * 短信内容
	 */
	private String content;
	/**
	 * 短信发送署名
	 */
	private String signName;
	/**
	 * 短信发送优先级
	 */
	private int sendPriority;
	/**
	 * 短信发送时间
	 */
	private Date sendTime;
	/**
	 * 短信发送状态
	 */
	private int sendFlag;
	
	/**
	 * 短信发送次数
	 */
	private int reSendNumber;
	
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	public String getDestNumber() {
		return destNumber;
	}
	public void setDestNumber(String destNumber) {
		this.destNumber = destNumber;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSignName() {
		return signName;
	}
	public void setSignName(String signName) {
		this.signName = signName;
	}
	public int getSendPriority() {
		return sendPriority;
	}
	public void setSendPriority(int sendPriority) {
		this.sendPriority = sendPriority;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public int getSendFlag() {
		return sendFlag;
	}
	public void setSendFlag(int sendFlag) {
		this.sendFlag = sendFlag;
	}
	public int getReSendNumber() {
		return reSendNumber;
	}
	public void setReSendNumber(int reSendNumber) {
		this.reSendNumber = reSendNumber;
	}
	
	
}
