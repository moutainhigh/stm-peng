package com.mainsteam.stm.system.um.relation.bo;

/**
 * <li>文件名称: UserDomain.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月20日
 * @author   ziwenwen
 */
public class UserDomain extends UmRelation {

	private static final long serialVersionUID = -3758342835240134457L;

	/**用户名 */
	private String userName;
	/**用户登录账号 */
	private String userAccount;
	/**用户状态*/
	private String userStatus;
	/**用户类型 */
	private int userType;
	
	private String domainName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}
}


