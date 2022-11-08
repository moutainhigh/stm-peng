package com.mainsteam.stm.portal.report.bo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("columnsData")
public class ColumnsData {
	
	@XStreamImplicit(itemFieldName="tableData")
	private List<TableData> tableData;

	public List<TableData> getTableData() {
		return tableData;
	}

	public void setTableData(List<TableData> tableData) {
		this.tableData = tableData;
	}
	
	
	
}
