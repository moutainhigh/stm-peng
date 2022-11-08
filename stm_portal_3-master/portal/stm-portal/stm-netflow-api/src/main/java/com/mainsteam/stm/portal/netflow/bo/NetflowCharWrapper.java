/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: DeviceFlowCharWrapper.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有: 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月29日
 * @author lil
 */
public class NetflowCharWrapper implements Serializable {

	/** 
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private List<NetflowChartBo> bos;
	private List<String> timeLine;
	private String sortColumn;
	private String yAxisName;
	private String unit;
	
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the bos
	 */
	public List<NetflowChartBo> getBos() {
		return bos;
	}

	/**
	 * @param bos
	 *            the bos to set
	 */
	public void setBos(List<NetflowChartBo> bos) {
		this.bos = bos;
	}

	/**
	 * @return the timeLine
	 */
	public List<String> getTimeLine() {
		return timeLine;
	}

	/**
	 * @param timeLine
	 *            the timeLine to set
	 */
	public void setTimeLine(List<String> timeLine) {
		this.timeLine = timeLine;
	}

	/**
	 * @return the sortColumn
	 */
	public String getSortColumn() {
		return sortColumn;
	}

	/**
	 * @param sortColumn the sortColumn to set
	 */
	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	/**
	 * @return the yAxisName
	 */
	public String getyAxisName() {
		return yAxisName;
	}

	/**
	 * @param yAxisName the yAxisName to set
	 */
	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}
	
}
