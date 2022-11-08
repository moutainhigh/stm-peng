package com.mainsteam.stm.portal.inspect.bo;

import java.util.List;

/**
 * <li>文件名称: InspectFrontReportOrPlanBo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>内容摘要: 巡检管理报表或计划模块前端请求数据封装类</li>
 * 
 * @version ms.stm
 * @since 2017年6月14日
 * @author sunhailiang
 */
public class InspectFrontReportOrPlanBo {
	/***
	 * 巡检报表结果及其结果描述集合类
	 */
	private List<InspectReportResultsSettingBo> reportResultsList;
	/**
	 * 巡检报表自定义选项集合
	 */
	private List<InspectReportSelfItemBo> reportSelfItemsList;
	/***
	 * 巡检计划结果及其结果描述集合类
	 */
	private List<InspectPlanResultSettingBo> planResultsList;
	/**
	 * 巡检计划自定义选项集合
	 */
	private List<InspectPlanSelfItemBo> planSelfItemsList;

	public List<InspectReportResultsSettingBo> getReportResultsList() {
		return reportResultsList;
	}

	public void setReportResultsList(
			List<InspectReportResultsSettingBo> reportResultsList) {
		this.reportResultsList = reportResultsList;
	}

	public List<InspectReportSelfItemBo> getReportSelfItemsList() {
		return reportSelfItemsList;
	}

	public void setReportSelfItemsList(
			List<InspectReportSelfItemBo> reportSelfItemsList) {
		this.reportSelfItemsList = reportSelfItemsList;
	}

	public List<InspectPlanResultSettingBo> getPlanResultsList() {
		return planResultsList;
	}

	public void setPlanResultsList(
			List<InspectPlanResultSettingBo> planResultsList) {
		this.planResultsList = planResultsList;
	}

	public List<InspectPlanSelfItemBo> getPlanSelfItemsList() {
		return planSelfItemsList;
	}

	public void setPlanSelfItemsList(
			List<InspectPlanSelfItemBo> planSelfItemsList) {
		this.planSelfItemsList = planSelfItemsList;
	}

}
