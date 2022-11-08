package com.mainsteam.stm.portal.inspect.bo;

import java.io.Serializable;
import java.util.List;

public class InspectPlanContentBo implements Serializable {
	private static final long serialVersionUID = -3077842805651999252L;
	// 表主键ID
	private Long id;
	// 外键，关联到inspect_plan表的主键
	private Long inspectPlanId;
	// 父ID，指向本表其它记录的ID
	private Long inspectPlanParentId;
	// 内容名称
	private String inspectPlanItemName;
	// 内容描述
	private String inspectPlanItemDescrible;
	// 巡检值
	private String inspectPlanItemValue;
	// 参考值前缀
	private String inspectPlanItemReferencePrefix;
	// 参考值后缀
	private String inspectPlanItemReferenceSubfix;
	// 单位
	private String inspectPlanItemUnit;
	// 情况概要
	private String itemConditionDescrible;
	// 排序
	private int sort = 0;
	// 是否是检查项
	private boolean edit;
	// 是否是指标作为巡检项
	private boolean indicatorAsItem;
	// 资源id
	private Long resourceId;
	// 指标id
	private String indexModelId;
	// 模型id
	private String modelId;
	// 1:部分自检，2：自检，3：手检
	private Integer type;
	// 子节点
	private List<InspectPlanContentBo> children;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getIndexModelId() {
		return indexModelId;
	}

	public void setIndexModelId(String indexModelId) {
		this.indexModelId = indexModelId;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public boolean isIndicatorAsItem() {
		return indicatorAsItem;
	}

	public void setIndicatorAsItem(boolean indicatorAsItem) {
		this.indicatorAsItem = indicatorAsItem;
	}

	public List<InspectPlanContentBo> getChildren() {
		return children;
	}

	public void setChildren(List<InspectPlanContentBo> children) {
		this.children = children;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInspectPlanId() {
		return inspectPlanId;
	}

	public void setInspectPlanId(Long inspectPlanId) {
		this.inspectPlanId = inspectPlanId;
	}

	public Long getInspectPlanParentId() {
		return inspectPlanParentId;
	}

	public void setInspectPlanParentId(Long inspectPlanParentId) {
		this.inspectPlanParentId = inspectPlanParentId;
	}

	public String getInspectPlanItemName() {
		return inspectPlanItemName;
	}

	public void setInspectPlanItemName(String inspectPlanItemName) {
		this.inspectPlanItemName = inspectPlanItemName;
	}

	public String getInspectPlanItemDescrible() {
		return inspectPlanItemDescrible;
	}

	public void setInspectPlanItemDescrible(String inspectPlanItemDescrible) {
		this.inspectPlanItemDescrible = inspectPlanItemDescrible;
	}

	public String getInspectPlanItemValue() {
		return inspectPlanItemValue;
	}

	public void setInspectPlanItemValue(String inspectPlanItemValue) {
		this.inspectPlanItemValue = inspectPlanItemValue;
	}

	public String getInspectPlanItemReferencePrefix() {
		return inspectPlanItemReferencePrefix;
	}

	public void setInspectPlanItemReferencePrefix(
			String inspectPlanItemReferencePrefix) {
		this.inspectPlanItemReferencePrefix = inspectPlanItemReferencePrefix;
	}

	public String getInspectPlanItemReferenceSubfix() {
		return inspectPlanItemReferenceSubfix;
	}

	public void setInspectPlanItemReferenceSubfix(
			String inspectPlanItemReferenceSubfix) {
		this.inspectPlanItemReferenceSubfix = inspectPlanItemReferenceSubfix;
	}

	public String getInspectPlanItemUnit() {
		return inspectPlanItemUnit;
	}

	public void setInspectPlanItemUnit(String inspectPlanItemUnit) {
		this.inspectPlanItemUnit = inspectPlanItemUnit;
	}

	public String getItemConditionDescrible() {
		return itemConditionDescrible;
	}

	public void setItemConditionDescrible(String itemConditionDescrible) {
		this.itemConditionDescrible = itemConditionDescrible;
	}

}
