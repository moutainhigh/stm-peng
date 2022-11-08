package com.mainsteam.stm.system.um.relation.bo;

/**
 * <li>文件名称: DomainRole.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月20日
 * @author   ziwenwen
 */
public class DomainRole extends UmRelation {
	private static final long serialVersionUID = -1382266281510367825L;
	
	private String domainName;
	private String roleName;
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}


