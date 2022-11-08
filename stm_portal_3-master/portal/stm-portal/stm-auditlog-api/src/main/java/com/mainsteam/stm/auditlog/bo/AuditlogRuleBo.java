package com.mainsteam.stm.auditlog.bo;

import java.util.Date;

/**
 * 
 * <li>文件名称: AuditlogRuleBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 业务逻辑层日志 备份规则的传输对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月21日
 * @author   jinsk
 */

public class AuditlogRuleBo extends AuditlogBo {

	private static final long serialVersionUID = 1L;
	
	private int backup_day;
	
	private String backup_time;
	
	private String status;
	
	private Date open_date;

	private String backupDateHour;
	
	private String backupDateMinute;
	
	private boolean isOpen;

	public int getBackup_day() {
		return backup_day;
	}

	public void setBackup_day(int backup_day) {
		this.backup_day = backup_day;
	}

	public String getBackup_time() {
		return backup_time;
	}

	public void setBackup_time(String backup_time) {
		this.backup_time = backup_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getOpen_date() {
		return open_date;
	}

	public void setOpen_date(Date open_date) {
		this.open_date = open_date;
	}

	public String getBackupDateHour() {
		return backupDateHour;
	}

	public void setBackupDateHour(String backupDateHour) {
		this.backupDateHour = backupDateHour;
	}

	public String getBackupDateMinute() {
		return backupDateMinute;
	}

	public void setBackupDateMinute(String backupDateMinute) {
		this.backupDateMinute = backupDateMinute;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	
}
