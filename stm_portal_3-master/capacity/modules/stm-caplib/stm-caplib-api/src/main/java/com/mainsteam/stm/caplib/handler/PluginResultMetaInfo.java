package com.mainsteam.stm.caplib.handler;

import org.apache.commons.lang.StringUtils;

/**
 * 列标题，在模型文件里配置是以逗号隔开
 * @author Administrator
 *
 */
public class PluginResultMetaInfo implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7185658545457072408L;
	private String[] columns;

	/**
	 * 获取插件采集结果的列信息
	 * @return
	 */
	public String[] getColumns() {
		return this.columns;
	}

	public void setColumns(String columns) {
		if(null == columns){
			return;
		}
		String[] array = StringUtils.split(columns,",");
		if(array != null){
			for(int i = 0; i < array.length; i++){
				
				array[i] = StringUtils.trim(array[i]);
			}
			this.columns = array;
		}
	}
}
