package com.mainsteam.stm.plugin.wmi.bean;

import java.util.ArrayList;

/**
 * 查询结果集的某个单元格数据
 * @author shugang
 *
 */
public class ResponseCell {

	/**
	 * Wmi查询结果中，返回的关键字
	 */
	private String wmi_res_key;
	
	/**
	 * 字符数组，数组的大小由wmi_res_value_count字段说明
	 */
	private ArrayList<String> wmi_res_value;
	
	/**
	 * wmi_res_value中，值的个数
	 */
	private int wmi_res_value_count;
		
	public String getWmi_res_key() {
		return wmi_res_key;
	}
	public void setWmi_res_key(String wmi_res_key) {
		this.wmi_res_key = wmi_res_key;
	}
	
	public int getWmi_res_value_count() {
		return wmi_res_value_count;
	}
	public void setWmi_res_value_count(int wmi_res_value_count) {
		this.wmi_res_value_count = wmi_res_value_count;
	}
	public ArrayList<String> getWmi_res_value() {
		return wmi_res_value;
	}
	public void setWmi_res_value(ArrayList<String> wmi_res_value) {
		this.wmi_res_value = wmi_res_value;
	}
	
	
}
