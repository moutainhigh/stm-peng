package com.mainsteam.stm.system.um.user.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.IRole;

/**
 * <li>文件名称: User.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月19日
 * @author   ziwenwen
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = -3859471584533563778L;
	
	private Long id;
	private Long creatorId;
	private int sex;
	private int userType;
	//0停用；1启用
	private int status;
	private Date createdTime;
	private String name;
	private String account;
	private String password;
	private String mobile;
	private String email;
	//for admin:
	private String phone;
	private List<IRole> roles;

	private int lockType;
	private Date lockTime;
	private Date upPassTime;
	private int passErrorCnt = 0;
	private String roleName;
	public User(){}
	public User(Long id, int status){
		this.id = id;
		this.status = status;
	}
	
	public void setUserType(int userType) {
		this.userType = userType;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getUserType() {
		return userType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@SuppressWarnings("unchecked")
	public void setRoles(List<? extends IRole> roles) {
		this.roles = (List<IRole>)roles;
	}
	
	public List<IRole> getRoles() {
		return roles;
	}
	
	public int getLockType() {
		return lockType;
	}
	public void setLockType(int lockType) {
		this.lockType = lockType;
	}
	public Date getLockTime() {
		return lockTime;
	}
	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}
	public int getPassErrorCnt() {
		return passErrorCnt;
	}
	public void setPassErrorCnt(int passErrorCnt) {
		this.passErrorCnt = passErrorCnt;
	}
	public Date getUpPassTime() {
		return upPassTime;
	}
	public void setUpPassTime(Date upPassTime) {
		this.upPassTime = upPassTime;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
}


