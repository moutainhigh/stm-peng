package com.mainsteam.stm.webService.sms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SmsBean")
public class SmsBean {

	@XmlElement(name = "destNumber", required = true)
	private String destNumber;

	@XmlElement(name = "content", required = true)
	private String content;

	@XmlElement(name = "sendTime", required = true)
	private String sendTime;

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

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	
}
