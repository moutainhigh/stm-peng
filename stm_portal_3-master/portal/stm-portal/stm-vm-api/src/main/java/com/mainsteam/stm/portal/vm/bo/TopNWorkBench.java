package com.mainsteam.stm.portal.vm.bo;

import java.io.Serializable;

/**
 * <li>文件名称: TopNWorkBench.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年4月3日
 * @author   caoyong
 */
public class TopNWorkBench implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	private String title;
	private String url;
	private String icon;
	private Long userId;
	private Long workbenchId;
	private int sort;
	private String selfExt;
	private Long domainId=1l;
	
	private String name;
	private String templateType;
	private String templateTypeName;
	private String sortMetric;
	private String sortMetricName;
	private String sortOrder;
	private Integer showType;
	private Integer TopNum;
	private String resourceIds;
//	NAME,TEMPLATE_TYPE,SORT_METRIC,SORT_ORDER,SHOW_TYPE,TOP_NUM,RESOURCE_IDS
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTemplateType() {
		return templateType;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	public String getSortMetric() {
		return sortMetric;
	}
	public void setSortMetric(String sortMetric) {
		this.sortMetric = sortMetric;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Integer getShowType() {
		return showType;
	}
	public void setShowType(Integer showType) {
		this.showType = showType;
	}
	public Integer getTopNum() {
		return TopNum;
	}
	public void setTopNum(Integer topNum) {
		TopNum = topNum;
	}
	public String getResourceIds() {
		return resourceIds;
	}
	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}
	public String getTemplateTypeName() {
		return templateTypeName;
	}
	public void setTemplateTypeName(String templateTypeName) {
		this.templateTypeName = templateTypeName;
	}
	public String getSortMetricName() {
		return sortMetricName;
	}
	public void setSortMetricName(String sortMetricName) {
		this.sortMetricName = sortMetricName;
	}
	
}


