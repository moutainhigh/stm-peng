package com.mainsteam.stm.portal.inspect.web.utils.entity;

import java.util.ArrayList;
import java.util.List;

public class Body {

	private String name;
	private List<String> titles;
	private List<Item> items;

	public Body() {
		titles = new ArrayList<>();
		titles.add("巡检项");
		titles.add("描述");
		titles.add("参照值");
		titles.add("巡检值");
		titles.add("巡检类型");
		titles.add("情况概要");
		titles.add("结果");
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

}
