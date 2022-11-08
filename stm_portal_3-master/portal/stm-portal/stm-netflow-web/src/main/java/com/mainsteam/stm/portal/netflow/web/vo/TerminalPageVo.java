package com.mainsteam.stm.portal.netflow.web.vo;

import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

/**
 * 
 * <li>文件名称: TerminalPageVo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月
 * @author xiejr
 */
public class TerminalPageVo implements BasePageVo {

	private static final long serialVersionUID = -2208435034457697723L;

	private long startRow;

	private long rowCount;

	private long totalRecord;

	private TerminalVo condtion;

	private List<TerminalVo> terminals;

	public long getStartRow() {
		return startRow;
	}

	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

	public long getTotalRecord() {
		return totalRecord;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public void setTotalRecord(long totalRecord) {
		this.totalRecord = totalRecord;
	}

	public TerminalVo getCondtion() {
		return condtion;
	}

	public void setCondtion(TerminalVo condtion) {
		this.condtion = condtion;
	}

	public List<TerminalVo> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<TerminalVo> terminals) {
		this.terminals = terminals;
	}

	@Override
	public long getTotal() {
		return this.totalRecord;
	}

	@Override
	public Collection<? extends Object> getRows() {
		return this.terminals;
	}

}
