/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.web.vo.BasePageVo;


/**
 * <li>文件名称: TerminalPageBo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public class ApplicationPageBo implements BasePageVo{
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private String sort;
	private List<ApplicationModelBo> rows;
	private ApplicationConditionBo appBo;
	

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

	public void setRows(List<ApplicationModelBo> rows) {
		this.rows = rows;
	}
	@Override
	public long getTotal() {
		// TODO Auto-generated method stub
		return this.totalRecord;
	}
	@Override
	public Collection<? extends Object> getRows() {
		if(rows!=null){
			return this.rows;
		}
		return new ArrayList<ApplicationModelBo>();
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public ApplicationConditionBo getAppBo() {
		return appBo;
	}

	public void setAppBo(ApplicationConditionBo appBo) {
		this.appBo = appBo;
	}
	
	
}
