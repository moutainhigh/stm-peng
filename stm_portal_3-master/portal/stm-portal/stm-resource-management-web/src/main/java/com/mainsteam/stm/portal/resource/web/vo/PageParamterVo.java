package com.mainsteam.stm.portal.resource.web.vo;

import java.util.List;

public class PageParamterVo {
	private String type;//元素类型 div label td ...
	private String width;//长度
	private String ComponentType;//组件类型
	private String valType;
	private String name;
	private String childtype;//子内容
	private String title;//标题
	List<PageParamterVo> childParamter;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getComponentType() {
		return ComponentType;
	}
	public void setComponentType(String componentType) {
		ComponentType = componentType;
	}
	public String getValType() {
		return valType;
	}
	public void setValType(String valType) {
		this.valType = valType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChildtype() {
		return childtype;
	}
	public void setChildtype(String childtype) {
		this.childtype = childtype;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<PageParamterVo> getChildParamter() {
		return childParamter;
	}
	public void setChildParamter(List<PageParamterVo> childParamter) {
		this.childParamter = childParamter;
	}
	
	
}
