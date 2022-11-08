package com.mainsteam.stm.portal.statist.bo;

import java.io.Serializable;
import java.util.List;

public class ChartTableBo extends ChartBaseBo implements Serializable {
	private static final long serialVersionUID = -183034691569322156L;
	private String type = "Table";
	private List<String> title;
	private List<String> head;
	private List<List<ChartTableTdBo>> data;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getTitle() {
		return title;
	}

	public void setTitle(List<String> title) {
		this.title = title;
	}

	public List<String> getHead() {
		return head;
	}

	public void setHead(List<String> head) {
		this.head = head;
	}

	public List<List<ChartTableTdBo>> getData() {
		return data;
	}

	public void setData(List<List<ChartTableTdBo>> data) {
		this.data = data;
	}

}
