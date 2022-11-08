package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>文件名称: User.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 控制层账户的传输对象</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月22日
 * @author lk
 */
public class AccountVo implements Serializable {
	private static final long serialVersionUID = 482569427032836788L;
	// 主键ID
	private Long account_id;
	private Long domain_id;
	private String domainname;
	// 用户名
	private String username;
	// 密码
	private String password;
	// 备注
	private String comments;
	// 状态(0无效/1有效)
	private String status;
	// 创建人ID
	private Long entry_id;
	// 创建时间
	private Date entry_datetime;
	// 删除时间
	private Date delete_datetime;

	private int resourses;

	public Long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}

	public Long getDomain_id() {
		return domain_id;
	}

	public void setDomain_id(Long domain_id) {
		this.domain_id = domain_id;
	}

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getEntry_id() {
		return entry_id;
	}

	public void setEntry_id(Long entry_id) {
		this.entry_id = entry_id;
	}

	public Date getEntry_datetime() {
		return entry_datetime;
	}

	public void setEntry_datetime(Date entry_datetime) {
		this.entry_datetime = entry_datetime;
	}

	public Date getDelete_datetime() {
		return delete_datetime;
	}

	public void setDelete_datetime(Date delete_datetime) {
		this.delete_datetime = delete_datetime;
	}

	public int getResourses() {
		return resourses;
	}

	public void setResourses(int resourses) {
		this.resourses = resourses;
	}

}
