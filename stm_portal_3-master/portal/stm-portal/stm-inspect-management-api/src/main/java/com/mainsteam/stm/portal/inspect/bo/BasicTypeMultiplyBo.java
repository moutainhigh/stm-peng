/**
 * 
 */
package com.mainsteam.stm.portal.inspect.bo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: BasicEveryMonthBo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 接口流量统计</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年11月4日
 * @author lil
 */
public class BasicTypeMultiplyBo implements Serializable {

	
	private static final long serialVersionUID = -1837667298072462666L;

	private String inspectTypeMultiYear;
	private String inspectTypeMultiMonth;
	private String inspectTypeMultiDate;
	private String inspectTypeMultiHour;
	private String inspectTypeMultiMinute;
	
	private List<String> inspectTypeMonthDate;
	private String inspectTypeHour;
	private String inspectTypeMinute;
	
	private String[] customDate;
	
	public BasicTypeMultiplyBo() {
		
	}
	
	/**
	 * @param inspectTypeMonthDate
	 * @param inspectTypeHour
	 * @param inspectTypeMinute
	 */
	public BasicTypeMultiplyBo(List<String> inspectTypeMonthDate,
			String inspectTypeHour, String inspectTypeMinute) {
		super();
		this.inspectTypeMonthDate = inspectTypeMonthDate;
		this.inspectTypeHour = inspectTypeHour;
		this.inspectTypeMinute = inspectTypeMinute;
	}

	public String[] getCustomDate() {
		return customDate;
	}

	public void setCustomDate(String[] customDate) {
		this.customDate = customDate;
	}

	/**
	 * @param inspectTypeMultiYear
	 * @param inspectTypeMultiMonth
	 * @param inspectTypeMultiDate
	 * @param inspectTypeMultiHour
	 * @param inspectTypeMultiMinute
	 */
	public BasicTypeMultiplyBo(String inspectTypeMultiYear,
			String inspectTypeMultiMonth, String inspectTypeMultiDate,
			String inspectTypeMultiHour, String inspectTypeMultiMinute) {
		super();
		this.inspectTypeMultiYear = inspectTypeMultiYear;
		this.inspectTypeMultiMonth = inspectTypeMultiMonth;
		this.inspectTypeMultiDate = inspectTypeMultiDate;
		this.inspectTypeMultiHour = inspectTypeMultiHour;
		this.inspectTypeMultiMinute = inspectTypeMultiMinute;
	}

	/**
	 * @return the inspectTypeMonthDate
	 */
	public List<String> getInspectTypeMonthDate() {
		return inspectTypeMonthDate;
	}

	/**
	 * @param inspectTypeMonthDate the inspectTypeMonthDate to set
	 */
	public void setInspectTypeMonthDate(List<String> inspectTypeMonthDate) {
		this.inspectTypeMonthDate = inspectTypeMonthDate;
	}

	/**
	 * @return the inspectTypeHour
	 */
	public String getInspectTypeHour() {
		return inspectTypeHour;
	}

	/**
	 * @param inspectTypeHour the inspectTypeHour to set
	 */
	public void setInspectTypeHour(String inspectTypeHour) {
		this.inspectTypeHour = inspectTypeHour;
	}

	/**
	 * @return the inspectTypeMinute
	 */
	public String getInspectTypeMinute() {
		return inspectTypeMinute;
	}

	/**
	 * @param inspectTypeMinute the inspectTypeMinute to set
	 */
	public void setInspectTypeMinute(String inspectTypeMinute) {
		this.inspectTypeMinute = inspectTypeMinute;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @param inspectTypeMultiDate
	 * @param inspectTypeMultiHour
	 * @param inspectTypeMultiMinute
	 */
	public BasicTypeMultiplyBo(String inspectTypeMultiDate,
			String inspectTypeMultiHour, String inspectTypeMultiMinute) {
		this(null, null, inspectTypeMultiDate, inspectTypeMultiHour, inspectTypeMultiMinute);
	}

	/**
	 * @return the inspectTypeMultiYear
	 */
	public String getInspectTypeMultiYear() {
		return inspectTypeMultiYear;
	}

	/**
	 * @param inspectTypeMultiYear
	 *            the inspectTypeMultiYear to set
	 */
	public void setInspectTypeMultiYear(String inspectTypeMultiYear) {
		this.inspectTypeMultiYear = inspectTypeMultiYear;
	}

	/**
	 * @return the inspectTypeMultiMonth
	 */
	public String getInspectTypeMultiMonth() {
		return inspectTypeMultiMonth;
	}

	/**
	 * @param inspectTypeMultiMonth
	 *            the inspectTypeMultiMonth to set
	 */
	public void setInspectTypeMultiMonth(String inspectTypeMultiMonth) {
		this.inspectTypeMultiMonth = inspectTypeMultiMonth;
	}

	/**
	 * @return the inspectTypeMultiDate
	 */
	public String getInspectTypeMultiDate() {
		return inspectTypeMultiDate;
	}

	/**
	 * @param inspectTypeMultiDate
	 *            the inspectTypeMultiDate to set
	 */
	public void setInspectTypeMultiDate(String inspectTypeMultiDate) {
		this.inspectTypeMultiDate = inspectTypeMultiDate;
	}

	/**
	 * @return the inspectTypeMultiHour
	 */
	public String getInspectTypeMultiHour() {
		return inspectTypeMultiHour;
	}

	/**
	 * @param inspectTypeMultiHour
	 *            the inspectTypeMultiHour to set
	 */
	public void setInspectTypeMultiHour(String inspectTypeMultiHour) {
		this.inspectTypeMultiHour = inspectTypeMultiHour;
	}

	/**
	 * @return the inspectTypeMultiMinute
	 */
	public String getInspectTypeMultiMinute() {
		return inspectTypeMultiMinute;
	}

	/**
	 * @param inspectTypeMultiMinute
	 *            the inspectTypeMultiMinute to set
	 */
	public void setInspectTypeMultiMinute(String inspectTypeMultiMinute) {
		this.inspectTypeMultiMinute = inspectTypeMultiMinute;
	}

}
