package com.mainsteam.stm.auditlog.bo;

import java.io.Serializable;
import java.util.Date;

public class AuditlogTemplet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7250181610641978056L;
	
	//ID index
		private long auditlogTempletId;
		//创建人
		private long auditlogTempletCreateUserId;
		
		private String auditlogTempletCreateUserName;
		//创建时间
		private Date auditlogTempletCreateTime;
		//相隔时间(天)
		private String auditlogTempletDay;
		//触发时间(小时)
		private String auditlogTempletHour;
		//触发时间(分钟)
		private String auditlogTempletMinute;
		//开始时间
		private String auditlogTempletBeginTime;
		//结束时间
		private String auditlogTempletEndTime;
		//生成时间:范围为0~24
		private int auditlogTempletGenerateTime;
		//是否启用:0.启用1.停用
		private String auditlogTempletStatus;

		private long auditlogTempletDomainId;

		public long getAuditlogTempletId() {
			return auditlogTempletId;
		}

		public void setAuditlogTempletId(long auditlogTempletId) {
			this.auditlogTempletId = auditlogTempletId;
		}

		public long getAuditlogTempletCreateUserId() {
			return auditlogTempletCreateUserId;
		}

		public void setAuditlogTempletCreateUserId(long auditlogTempletCreateUserId) {
			this.auditlogTempletCreateUserId = auditlogTempletCreateUserId;
		}

		public String getAuditlogTempletCreateUserName() {
			return auditlogTempletCreateUserName;
		}

		public void setAuditlogTempletCreateUserName(
				String auditlogTempletCreateUserName) {
			this.auditlogTempletCreateUserName = auditlogTempletCreateUserName;
		}

		public Date getAuditlogTempletCreateTime() {
			return auditlogTempletCreateTime;
		}

		public void setAuditlogTempletCreateTime(Date auditlogTempletCreateTime) {
			this.auditlogTempletCreateTime = auditlogTempletCreateTime;
		}

		public String getAuditlogTempletBeginTime() {
			return auditlogTempletBeginTime;
		}

		public void setAuditlogTempletBeginTime(String auditlogTempletBeginTime) {
			this.auditlogTempletBeginTime = auditlogTempletBeginTime;
		}

		public String getAuditlogTempletEndTime() {
			return auditlogTempletEndTime;
		}

		public void setAuditlogTempletEndTime(String auditlogTempletEndTime) {
			this.auditlogTempletEndTime = auditlogTempletEndTime;
		}

		public int getAuditlogTempletGenerateTime() {
			return auditlogTempletGenerateTime;
		}

		public void setAuditlogTempletGenerateTime(int auditlogTempletGenerateTime) {
			this.auditlogTempletGenerateTime = auditlogTempletGenerateTime;
		}

		public String getAuditlogTempletStatus() {
			return auditlogTempletStatus;
		}

		public void setAuditlogTempletStatus(String auditlogTempletStatus) {
			this.auditlogTempletStatus = auditlogTempletStatus;
		}

		public long getAuditlogTempletDomainId() {
			return auditlogTempletDomainId;
		}

		public void setAuditlogTempletDomainId(long auditlogTempletDomainId) {
			this.auditlogTempletDomainId = auditlogTempletDomainId;
		}

		public String getAuditlogTempletDay() {
			return auditlogTempletDay;
		}

		public void setAuditlogTempletDay(String auditlogTempletDay) {
			this.auditlogTempletDay = auditlogTempletDay;
		}

		public String getAuditlogTempletHour() {
			return auditlogTempletHour;
		}

		public void setAuditlogTempletHour(String auditlogTempletHour) {
			this.auditlogTempletHour = auditlogTempletHour;
		}

		public String getAuditlogTempletMinute() {
			return auditlogTempletMinute;
		}

		public void setAuditlogTempletMinute(String auditlogTempletMinute) {
			this.auditlogTempletMinute = auditlogTempletMinute;
		}

}
