package com.mainsteam.stm.portal.resource.bo;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;

/**
 * <li>文件名称: ResourceMonitorPageBo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有: 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月30日
 * @author xhf
 */
public class ResourceMonitorPageBo {
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<ResourceMonitorBo> resourceMonitorBosExtends;
	private List<ResourceInstance> resourceMonitorBos;
	private List<ResourceCategoryBo> ResourceCategoryBos;

	public List<ResourceMonitorBo> getResourceMonitorBosExtends() {
		return resourceMonitorBosExtends;
	}

	public void setResourceMonitorBosExtends(
			List<ResourceMonitorBo> resourceMonitorBosExtends) {
		this.resourceMonitorBosExtends = resourceMonitorBosExtends;
	}

	public List<ResourceInstance> getResourceMonitorBos() {
		return resourceMonitorBos;
	}

	public void setResourceMonitorBos(List<ResourceInstance> resourceMonitorBos) {
		this.resourceMonitorBos = resourceMonitorBos;
	}

	public List<ResourceCategoryBo> getResourceCategoryBos() {
		return ResourceCategoryBos;
	}

	public void setResourceCategoryBos(
			List<ResourceCategoryBo> resourceCategoryBos) {
		ResourceCategoryBos = resourceCategoryBos;
	}

	public long getStartRow() {
		return startRow;
	}

	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

}
