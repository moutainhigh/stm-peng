package com.mainsteam.stm.portal.resource.web.vo;

import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

/**
 * <li>文件名称: ResourceCategoryPageVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   pengl
 */
public class ResourceCategoryPageVo implements BasePageVo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -743347215513863410L;
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private ResourceCategoryVo condition;
	private List<ResourceCategoryVo> ResourceCategorys;
	
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
	public ResourceCategoryVo getCondition() {
		return condition;
	}
	public void setCondition(ResourceCategoryVo condition) {
		this.condition = condition;
	}
	public List<ResourceCategoryVo> getResourceCategorys() {
		return ResourceCategorys;
	}
	public void setResourceCategorys(List<ResourceCategoryVo> resourceCategorys) {
		ResourceCategorys = resourceCategorys;
	}
	@Override
	public long getTotal() {
		// TODO Auto-generated method stub
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		// TODO Auto-generated method stub
		return this.ResourceCategorys;
	}
	
	
	

}
