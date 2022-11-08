package com.mainsteam.stm.portal.inspect.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.inspect.bo.InspectFrontReportOrPlanBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportConditionBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportResultsSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportSelfItemBo;

public interface IInspectReportApi {
	public List<InspectReportBasicBo> getReportList(
			Page<InspectReportBasicBo, InspectReportConditionBo> page);

	public int updateStatus(String id);

	public InspectReportBasicBo loadBasic(long id);

	public List<InspectReportSelfItemBo> loadRoutine(long id);

	public List<InspectReportResultsSettingBo> getConclusionsByBasicId(long id);

	public List<InspectReportContentBo> loadInspectionItems(long id);

	public List<InspectReportContentBo> loadItem(long catalogId);

	List<InspectReportContentBo> loadItemTwo(long catalogId);

	int saveBasic(InspectReportBasicBo data);

	int saveSelfItem(List<InspectReportSelfItemBo> reportSelfs);

	int saveResults(List<InspectReportResultsSettingBo> results);

	int saveContents(List<InspectReportContentBo> contents);

	/**
	 * 更新巡检报告基本信息
	 *
	 * @param bo
	 * @return
	 */
	int updateBasic(InspectReportBasicBo bo);

	/**
	 * 更新巡检报告常规信息
	 *
	 * @param id
	 * @param inspectReportSelfItemName
	 * @param inspectReportSelfItemType
	 * @param inspectReportItemContent
	 * @return
	 */
	int updateNormal(InspectFrontReportOrPlanBo requestBo);

	/**
	 * 更新巡检报告结论
	 *
	 * @param id
	 * @param inspectReportSumeriseDescrible
	 * @return
	 */
	int updateResult(InspectFrontReportOrPlanBo bo);

	/**
	 * 更新巡检项
	 *
	 * @param items
	 *            json字符串
	 * @return
	 */
	int updateItems(String items);

	int updateEditDate(long id);
}
