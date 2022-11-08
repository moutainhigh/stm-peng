/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

/**
 * <li>文件名称: DevicePageBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月26日
 * @author   lil
 */
public class NetflowPageBo implements BasePageVo {
	
	private static final long serialVersionUID = 1L;
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private List<NetflowBo> rows;
	
	private NetflowParamBo paramBo;
	
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
		if(null != rows) {
			return this.rows;
		}
		return new ArrayList<NetflowBo>();
	}
	/**
	 * @return the totalRecord
	 */
	public long getTotalRecord() {
		return totalRecord;
	}
	/**
	 * @param totalRecord the totalRecord to set
	 */
	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}
	/**
	 * @return the startRow
	 */
	public long getStartRow() {
		return startRow;
	}
	/**
	 * @return the rowCount
	 */
	public long getRowCount() {
		return rowCount;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<NetflowBo> rows) {
		this.rows = rows;
	}
	/**
	 * @return the paramBo
	 */
	public NetflowParamBo getParamBo() {
		return paramBo;
	}
	/**
	 * @param paramBo the paramBo to set
	 */
	public void setParamBo(NetflowParamBo paramBo) {
		this.paramBo = paramBo;
	}

}
