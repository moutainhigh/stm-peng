package com.mainsteam.stm.system.um.user.vo;

/**
 * <li>文件名称: UserConditionVo.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月2日
 */
public class UserConditionVo {
	private Long[] domainId;
	private String keyword;
	private Integer userType;
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	public Long[] getDomainId() {
		return domainId;
	}
	public void setDomainId(Long[] domainId) {
		this.domainId = domainId;
	}
}
