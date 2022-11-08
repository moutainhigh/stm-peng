/**
 * 
 */
package com.mainsteam.stm.resourcelog.syslog.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

/**
 * <li>文件名称: SyslongResourcePageBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月12日
 * @author   lil
 */
public class SyslogResourcePageBo implements Serializable, BasePageVo {
	
	private static final long serialVersionUID = 8300929851696404037L;
	
	private String domain;
	
	private long startRow;
	private long rowCount;
	private long totalRecord;
	private SyslogResourceBo condition;
	private List<SyslogResourceBo> logList;
	
	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}
	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
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
		if(null == logList) {
			return new ArrayList<SyslogResourceBo>();
		}
		return logList;
	}
	/**
	 * @param logList the logList to set
	 */
	public void setLogList(List<SyslogResourceBo> logList) {
		this.logList = logList;
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
	 * @return the condition
	 */
	public SyslogResourceBo getCondition() {
		return condition;
	}
	/**
	 * @param condition the condition to set
	 */
	public void setCondition(SyslogResourceBo condition) {
		this.condition = condition;
	}

}
