package com.mainsteam.stm.export.data;

import java.util.ArrayList;
import java.util.List;

public class DataTable {

	private String name;

	private List<DataRow> rows = new ArrayList<DataRow>();

	private DataRow hand = new DataRow();

	private int columnSize = 0;

	public void setColumnSize(int columnSize) {
		if (columnSize < 0) {
			throw new RuntimeException("columSize Must be greater than 0!");
		}
		this.columnSize = columnSize;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataTable(DataRow hand) {
		this.setHand(hand);
		this.setColumnSize(hand.getRow().size());
	}

	public DataTable(int columSize) {
		this.setColumnSize(columSize);
	}

	public DataTable(String name) {
		this.setName(name);
	}

	public DataTable(String name, DataRow hand) {
		this.setName(name);
		this.hand = hand;
		this.setColumnSize(hand.getRow().size());
	}

	public DataTable(String name, int columSize) {
		this.setName(name);
		this.setColumnSize(columSize);
	}

	public void setHand(DataRow hand) {
		if (hand != null && hand.getRow().size() > this.columnSize) {
			throw new RuntimeException("[DataRow] row Data out of range!");
		}
		this.hand = hand;
	}

	public DataRow getHand() {
		return hand;
	}

	public void addRow(DataRow row) {
		if (row != null && row.getRow().size() > this.columnSize) {
			throw new RuntimeException("[DataRow] row Data out of range!");
		}
		this.rows.add(row);
	}

	public List<DataRow> getRows() {
		return rows;
	}

}
