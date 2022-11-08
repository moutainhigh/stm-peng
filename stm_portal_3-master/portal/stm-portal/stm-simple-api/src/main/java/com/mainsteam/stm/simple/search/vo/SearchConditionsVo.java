package com.mainsteam.stm.simple.search.vo;

import java.util.List;

/**
 * <li>文件名称: com.mainsteam.stm.simple.search.vo.SearchConditionsVo.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月29日
 */
public class SearchConditionsVo {

	private String keyword;
	private Boolean isIP;
	private Long type;
	private List<Long> resourceIds;
	//还需要controller层提供页签权限
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Boolean getIsIP() {
		return isIP;
	}
	public void setIsIP(Boolean isIP) {
		this.isIP = isIP;
	}
	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}
	public List<Long> getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(List<Long> resourceIds) {
		this.resourceIds = resourceIds;
	}
	
}
