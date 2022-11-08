package com.mainsteam.stm.home.workbench.main.bo;

import java.io.Serializable;

/**
 * <li>文件名称: WorkBench.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月10日
 * @author   ziwenwen
 */
public class WorkBench implements Serializable{
	private static final long serialVersionUID = -7137247108077527245L;
	private Long id;
	private String title;
	private String url;
	private String icon;
	private Long userId;
	private Long workbenchId;
	private int sort;
	private String selfExt;
	private String selfExt1;
	private Long domainId=1l;
	private Long defaultId;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getWorkbenchId() {
		return workbenchId;
	}
	public void setWorkbenchId(Long workbenchId) {
		this.workbenchId = workbenchId;
	}
	public String getSelfExt() {
		return selfExt;
	}
	public void setSelfExt(String selfExt) {
		this.selfExt = selfExt;
	}
	public String getSelfExt1() {
		return selfExt1;
	}
	public void setSelfExt1(String selfExt1) {
		this.selfExt1 = selfExt1;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public Long getDefaultId() {
		return defaultId;
	}
	public void setDefaultId(Long defaultId) {
		this.defaultId = defaultId;
	}
	
}


