package com.mainsteam.stm.alarm.obj;

import java.io.Serializable;
import java.util.Date;

/**
 * Alarm notify template
 * @author Xiaopf
 */
public class SmsOrEmailNotifyTemplate implements Comparable, Serializable{
	private long templateID;
	private String templateName;
	private NotifyTypeEnum templateType;
	private String content;
	private String title;
	private boolean isDefaultTemplate;
	private long provider;
	private Date updateTime;
	private SysModuleEnum sysModuleEnum;

	public long getTemplateID() {
		return templateID;
	}

	public void setTemplateID(long templateID) {
		this.templateID = templateID;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public NotifyTypeEnum getTemplateType() {
		return templateType;
	}

	public void setTemplateType(NotifyTypeEnum templateType) {
		this.templateType = templateType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isDefaultTemplate() {
		return isDefaultTemplate;
	}

	public void setDefaultTemplate(boolean defaultTemplate) {
		isDefaultTemplate = defaultTemplate;
	}

	public long getProvider() {
		return provider;
	}

	public void setProvider(long provider) {
		this.provider = provider;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public SysModuleEnum getSysModuleEnum() {
		return sysModuleEnum;
	}

	public void setSysModuleEnum(SysModuleEnum sysModuleEnum) {
		this.sysModuleEnum = sysModuleEnum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "SmsOrEmailNotifyTemplate{" +
				"templateID=" + templateID +
				", templateName='" + templateName + '\'' +
				", templateType=" + templateType +
				", content='" + content + '\'' +
				", title='" + title + '\'' +
				", isDefaultTemplate=" + isDefaultTemplate +
				", provider=" + provider +
				", updateTime=" + updateTime +
				", sysModuleEnum=" + sysModuleEnum +
				'}';
	}

	@Override
	public int compareTo(Object o) {
		SmsOrEmailNotifyTemplate other = (SmsOrEmailNotifyTemplate)o;
		if(this.getTemplateID() > other.getTemplateID() || this.getUpdateTime().after(((SmsOrEmailNotifyTemplate) o).getUpdateTime()))
			return 1;
		else
			return -1;
	}

}
