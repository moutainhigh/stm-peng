package com.mainsteam.stm.webService.obj;

import java.util.Date;

import com.mainsteam.stm.system.um.user.bo.User;

public class ItsmUserBean {
	/**
	 * 用户ID
	 */
	private String id;
	/**
	 * 用户登录账户
	 */
	private String loginid;
	/**
	 * 用户姓名
	 */
	private String name;
	/**
	 * 用户密码
	 */
	private String password;
	/**
	 * 用户编号
	 */
	private String usercode;
	/**
	 * 性别
	 */
	private String sex;
	/**
	 * 生日
	 */
	private Date birthday;
	/**
	 * 所属组织ID
	 */
	private String organizationId;
	/**
	 * 邮箱
	 */
	private String email; 
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 电话号码
	 */
	private String phone;
	/**
	 * 在职状态
	 *  0-	离职
	 *  1-	在职（默认）
	 */
	private String active;
	/**
	 * 账户有效状态
	 *  0-	无效
	 *  1-	有效（默认)
	 */
	private String available;
	/**
	 * 区域ID
	 */
	private String[] areaid;
	/**
	 * 区域名称
	 */
	private String[] areaname;
	/**
	 * 操作类型
	 *  1.INSERT-新增；
		2.UPDATE-修改；
		3.DELETE-删除
	 */
	private String operatetype;
	public ItsmUserBean() {
	}
	public ItsmUserBean(User user){
		//ID
		this.id = String.valueOf(user.getId());
		//密码
		this.password = user.getPassword();
		//姓名
		this.name = user.getName();
		//邮箱
		this.email = user.getEmail();
		//账号
		this.loginid = user.getAccount();
		//可用性
		this.available = String.valueOf(user.getStatus());
		//手机
		this.mobile = user.getMobile();
		//电话
		this.phone = "";
		//性别
		this.sex = String.valueOf(Math.abs(user.getSex()-1));
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsercode() {
		return usercode;
	}
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getAvailable() {
		return available;
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	public String[] getAreaid() {
		return areaid;
	}
	public void setAreaid(String[] areaid) {
		this.areaid = areaid;
	}
	public String[] getAreaname() {
		return areaname;
	}
	public void setAreaname(String[] areaname) {
		this.areaname = areaname;
	}
	public String getOperatetype() {
		return operatetype;
	}
	public void setOperatetype(String operatetype) {
		this.operatetype = operatetype;
	}
}
