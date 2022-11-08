package com.mainsteam.stm.export.data;

import java.util.ArrayList;
import java.util.List;

public class DataRow {

	private List<String> row = new ArrayList<String>();

	public void addColumn(String value) {
		this.row.add(value);
	}

	public List<String> getRow() {
		return row;
	}

	public void setRow(List<String> row) {
		this.row = row;
	}

}
