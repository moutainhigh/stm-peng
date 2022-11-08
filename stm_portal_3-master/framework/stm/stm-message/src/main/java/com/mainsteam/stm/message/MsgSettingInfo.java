package com.mainsteam.stm.message;

/**
 * <li>文件名称: AlarmInfo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 告警设置信息</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月18日
 * @author   zhangjunfeng
 */
public class MsgSettingInfo {

	/**邮件告警设置*/
	
	/** SMTP服务器*/
	private String emailSmtp;
	/**端口*/
	private String emailPort;
	/**发送邮箱*/
	private String emailSendEmail;
	/**发送账号*/
	private String emailAccount;
	/**密码*/
	private String emailPassword;
	
	/**短信告警设置 */
	/**发送方式 :短信猫|短信网关*/
	private String messageSendType;
	/**短信猫类型: GSM Moderm/CDMA Moderm/GPRS Moderm|短信网关类型： 联通SGIP/移动CMPP/电信SMGP*/
	private String messageType;
//	/**COM端口号*/
//	private String messageCOMPort;
//	/**波特率*/
//	private String messageBaudRate;
//	/**SIM卡PIN码 */
//	private String messageSIMPIN;
	
	/**
	 * 短信客户端IP
	 */
	private String clientIp;
	/**
	 * 短信客户端端口
	 */
	private String clientPort;
	
	/**
	 * moderm 制造商
	 * */
	private String manufacturer;
	
	/**
	 * moderm 型号
	 * */
	private String model;
	
	/**网关IP地址*/
	private String messageGatewayIp;
	/**端口*/
	private String messagePort;
	/**登录帐号*/
	private String messageLoginAccount;
	/**登录密码*/
	private String messagePassword;
	
	
	/**由短信网关分配的SPCODE,即用户接受到的短信显示的主叫号码*/
	private String spCode;
	
	/**SOCKET超时链接时间，可根据需求自由修改，建议6000，单位为毫秒*/
	private String timeOut;
	
	/**SOCKET链接失败重试次数，及短信发送失败重新发送的次数*/
	private String connectCount;
	
	private String time;//告警抑制期
	public String getEmailSmtp() {
		return emailSmtp;
	}
	public void setEmailSmtp(String emailSmtp) {
		this.emailSmtp = emailSmtp;
	}
	public String getEmailPort() {
		return emailPort;
	}
	public void setEmailPort(String emailPort) {
		this.emailPort = emailPort;
	}
	public String getEmailSendEmail() {
		return emailSendEmail;
	}
	public void setEmailSendEmail(String emailSendEmail) {
		this.emailSendEmail = emailSendEmail;
	}
	
	public String getEmailAccount() {
		return emailAccount;
	}
	public void setEmailAccount(String emailAccount) {
		this.emailAccount = emailAccount;
	}
	public String getEmailPassword() {
		return emailPassword;
	}
	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}
	
	public String getMessageSendType() {
		return messageSendType;
	}
	public void setMessageSendType(String messageSendType) {
		this.messageSendType = messageSendType;
	}
	
	public String getMessageGatewayIp() {
		return messageGatewayIp;
	}
	public void setMessageGatewayIp(String messageGatewayIp) {
		this.messageGatewayIp = messageGatewayIp;
	}
	public String getMessagePort() {
		return messagePort;
	}
	public void setMessagePort(String messagePort) {
		this.messagePort = messagePort;
	}
	public String getMessageLoginAccount() {
		return messageLoginAccount;
	}
	public void setMessageLoginAccount(String messageLoginAccount) {
		this.messageLoginAccount = messageLoginAccount;
	}
	public String getMessagePassword() {
		return messagePassword;
	}
	public void setMessagePassword(String messagePassword) {
		this.messagePassword = messagePassword;
	}
	
	public String getSpCode() {
		return spCode;
	}
	public void setSpCode(String spCode) {
		this.spCode = spCode;
	}
	public String getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}
	public String getConnectCount() {
		return connectCount;
	}
	public void setConnectCount(String connectCount) {
		this.connectCount = connectCount;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getClientPort() {
		return clientPort;
	}
	public void setClientPort(String clientPort) {
		this.clientPort = clientPort;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	
}
