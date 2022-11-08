/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: DeviceAppChartBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月17日
 * @author   lil
 */
public class NetflowChartBo implements Serializable {

	/** 
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private List<Double> data;
	
	public NetflowChartBo() {
		
	}
	
	/**
	 * @param name
	 * @param data
	 */
	public NetflowChartBo(String name, List<Double> data) {
		super();
		this.name = name;
		this.data = data;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the data
	 */
	public List<Double> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(List<Double> data) {
		this.data = data;
	}
	
}
