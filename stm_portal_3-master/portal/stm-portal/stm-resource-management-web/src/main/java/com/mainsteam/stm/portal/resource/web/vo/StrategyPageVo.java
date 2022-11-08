package com.mainsteam.stm.portal.resource.web.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
/**
 * <li>文件名称: StrategyPageVo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li> 
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月
 * @author xhf
 */
public class StrategyPageVo implements BasePageVo{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	private long startRow;
	private long rowCount;
	private long totalRecord;
	private ProfileInfo condition;
	private String sort;
	private String order;
	private List<StrategyVo> strategys;
	
	
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

	

	public List<StrategyVo> getStrategys() {
		return strategys;
	}

	public void setStrategys(List<StrategyVo> strategys) {
		this.strategys = strategys;
	}

	public ProfileInfo getCondition() {
		return condition;
	}

	public void setCondition(ProfileInfo condition) {
		this.condition = condition;
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
		if(strategys != null){
			return this.strategys;
		}
		return new ArrayList<StrategyVo>();
	}



}
