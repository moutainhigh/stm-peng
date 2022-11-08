package com.mainsteam.stm.caplib.plugin;

import org.apache.commons.lang.StringUtils;

public class SupportValue implements java.io.Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2911959911565863349L;

	private String name;
	private String value;
	/**
	 * 如果选择了这个值,显示哪个组
	 */
	private String showGroup;
	/**
	 * 隐藏哪个组
	 */
	private String hideGroup;

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取value
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 获取显示组名
	 * 
	 * @return
	 */
	public String getShowGroup() {
		return showGroup;
	}

	public String[] getShowGroups() {
		return StringUtils.split(showGroup, ',');
	}

	public void setShowGroup(String showGroup) {
		this.showGroup = showGroup;
	}

	/**
	 * 获取隐藏组名
	 * 
	 * @return
	 */
	public String getHideGroup() {
		return hideGroup;
	}

	/**
	 * 获取隐藏组名
	 * 
	 * @return
	 */
	public String[] getHideGroups() {
		return StringUtils.split(hideGroup, ',');
	}

	public void setHideGroup(String hideGroup) {
		this.hideGroup = hideGroup;
	}
	
	@Override
	public Object clone(){
		SupportValue def = new SupportValue();
		def.setHideGroup(this.getHideGroup());
		def.setName(this.getName());
		def.setShowGroup(this.getShowGroup());
		def.setValue(this.getValue());
		return def;
	}

}
