package com.mainsteam.stm.auditlog.bo;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 * <li>文件名称: LogBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 业务逻辑层日志的传输对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月5日  上午11:04:51
 * @author   xch
 */
public class AuditlogBo implements Serializable{
	private static final long serialVersionUID = 5192824042974294104L;
	private Long id;
	/**
	 * 操作人
	 */
	private String oper_user;
	/**
	 * 操作IP
	 */
	private String oper_ip;
	/**
	 * 操作模块
	 */
	private String oper_module;
	
	/**
	 * 操作模块ID
	 */
	private Long oper_module_id;
	/**
	 * 操作类型
	 */
	private String oper_type;
	/**
	 * 操作对象
	 */
	private String oper_object;
	/**
	 * 操作时间
	 */
	private Date oper_date;
	/**
	 * 删除标识
	 */
	private String del_status;
	/**
	 * 备份日期
	 */
	private Date backup_date;
	public String getOper_user() {
		return oper_user;
	}
	public void setOper_user(String oper_user) {
		this.oper_user = oper_user;
	}
	public String getOper_ip() {
		return oper_ip;
	}
	public void setOper_ip(String oper_ip) {
		this.oper_ip = oper_ip;
	}
	public String getOper_module() {
		return oper_module;
	}
	public void setOper_module(String oper_module) {
		this.oper_module = oper_module;
	}
	public String getOper_type() {
		return oper_type;
	}
	public void setOper_type(String oper_type) {
		this.oper_type = oper_type;
	}
	public String getOper_object() {
		return oper_object;
	}
	public void setOper_object(String oper_object) {
		this.oper_object = oper_object;
	}
	public Date getOper_date() {
		return oper_date;
	}
	public void setOper_date(Date oper_date) {
		this.oper_date = oper_date;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOper_module_id() {
		return oper_module_id;
	}
	public void setOper_module_id(Long oper_module_id) {
		this.oper_module_id = oper_module_id;
	}
	public String getDel_status() {
		return del_status;
	}
	public void setDel_status(String del_status) {
		this.del_status = del_status;
	}
	public Date getBackup_date() {
		return backup_date;
	}
	public void setBackup_date(Date backup_date) {
		this.backup_date = backup_date;
	}


}
