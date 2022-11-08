package com.mainsteam.stm.message.smsmodem;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>文件名称: SentRecord</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 已发送短信记录</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日 下午7:18:56
 * @author   俊峰
 */
public class SentRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7362410006348155030L;
	/**
	 * 发送成功 
	 */
	public static int MESSAGE_SEND_FLAG_SUCCESS =1;
	/**
	 * 发送失败
	 */
	public static int MESSAGE_SEND_FLAG_FAIL=2;
	
	/**
	 * 发送短信ID
	 */
	private long msgId;
	
	/**
	 * 短信所属任务ID
	 */
	private long taskId;
	/**
	 * 短信接收人电话号码 
	 */
	private String destTel;
	/**
	 * 短信发送内容
	 */
	private String content;
	/**
	 * 短信发送时间
	 */
	private Date sendTime;
	/**
	 * 短信发送状态
	 */
	private int sendFlag;
	public long getMsgId() {
		return msgId;
	}
	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}
	public String getDestTel() {
		return destTel;
	}
	public void setDestTel(String destTel) {
		this.destTel = destTel;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
}
