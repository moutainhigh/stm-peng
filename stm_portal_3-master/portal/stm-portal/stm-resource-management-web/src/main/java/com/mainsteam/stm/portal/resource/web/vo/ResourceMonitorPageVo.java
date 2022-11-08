package com.mainsteam.stm.portal.resource.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.vo.BasePageVo;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;

/**
 * <li>文件名称: ResourceMonitorPageVo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月28日
 * @author xhf
 */
public class ResourceMonitorPageVo implements BasePageVo{
	

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private ResourceMonitorVo condition;
	private List<ResourceMonitorVo> resourceMonitors;
	private List<ResourceCategoryBo> ResourceCategoryBos;
	private String sort;
	private String order;
	

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public ResourceMonitorVo getCondition() {
		return condition;
	}

	public void setCondition(ResourceMonitorVo condition) {
		this.condition = condition;
	}

	public List<ResourceMonitorVo> getResourceMonitors() {
		return resourceMonitors;
	}

	public void setResourceMonitors(List<ResourceMonitorVo> resourceMonitors) {
		this.resourceMonitors = resourceMonitors;
	}

	public List<ResourceCategoryBo> getResourceCategoryBos() {
		return ResourceCategoryBos;
	}

	public void setResourceCategoryBos(List<ResourceCategoryBo> resourceCategoryBos) {
		ResourceCategoryBos = resourceCategoryBos;
	}

	public long getStartRow() {
		return startRow;
	}

	public long getRowCount() {
		return rowCount;
	}

	@Override
	public void setStartRow(long startRow) {	
		this.startRow = startRow;
	}

	@Override
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	@Override
	public long getTotal() {
		
		return this.totalRecord;
	}
	
	@Override
	public Collection<? extends Object> getRows() {
		if(resourceMonitors != null){
			return this.resourceMonitors;
		}
		return new ArrayList<ResourceMonitorVo>();
	}

}
