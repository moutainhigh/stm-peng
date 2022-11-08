package com.mainsteam.stm.portal.report.bo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("columnsTitle")
public class ColumnsTitle {
	
	@XStreamImplicit(itemFieldName="columns")
	private List<Columns> columns;

	public List<Columns> getColumns() {
		return columns;
	}

	public void setColumns(List<Columns> columns) {
		this.columns = columns;
	}	
	
	
}
