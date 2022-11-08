package com.mainsteam.stm.home.workbench.resource.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageResource implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<String> categories = new ArrayList<String>();;
	private List<Integer> data = new ArrayList<Integer>();
	private Integer total = 0;
	private Integer categoriesCount = 0;
	public Integer getCategoriesCount() {
		return categoriesCount;
	}
	public void setCategoriesCount(Integer categoriesCount) {
		this.categoriesCount = categoriesCount;
	}
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	public List<Integer> getData() {
		return data;
	}
	public void setData(List<Integer> data) {
		this.data = data;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	

}
