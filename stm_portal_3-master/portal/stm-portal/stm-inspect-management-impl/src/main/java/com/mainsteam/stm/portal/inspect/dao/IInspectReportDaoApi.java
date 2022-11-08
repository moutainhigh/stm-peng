package com.mainsteam.stm.portal.inspect.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.inspect.bo.InspectReportBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportConditionBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportResultsSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportSelfItemBo;

public interface IInspectReportDaoApi {
	public List<InspectReportBasicBo> getReportList(
			Page<InspectReportBasicBo, InspectReportConditionBo> page);

	public int updateStatus(String id);

	public InspectReportBasicBo loadBasic(long id);

	public List<InspectReportSelfItemBo> loadRoutine(long id);

	public List<InspectReportResultsSettingBo> getConclusionsByBasicId(long id);

	public List<InspectReportContentBo> loadInspectionItems(long id);

	public List<InspectReportContentBo> loadItem(long catalogId);

	int saveBasic(InspectReportBasicBo data);

	int saveSelfItem(List<InspectReportSelfItemBo> reportSelfs);

	int saveResults(List<InspectReportResultsSettingBo> results);

	int saveContents(List<InspectReportContentBo> contents);
	int addContents(List<InspectReportContentBo> contents);

	/**
	 * 更新巡检报告基本信息
	 *
	 * @param bo
	 * @return
	 */
	int updateBasic(InspectReportBasicBo bo);

	/**
	 * 根据报告ID，删除自定义显示项
	 *
	 * @param id
	 * @return
	 */
	int deleteNormalByReportId(Long id);

	/**
	 * 添加报告的自定义显示项
	 *
	 * @param bos
	 * @return
	 */
	int updateReportSelfItems(InspectReportSelfItemBo bo);

	/**
	 * 更新巡检报告结果
	 *
	 * @param bo
	 */
	int updateResult(InspectReportResultsSettingBo bo);

	/**
	 * 更新巡检项中的寻兼职，高腰情况，巡检结果
	 *
	 * @param id
	 * @param value
	 * @param describle
	 * @param result
	 * @return
	 */
	int updateItemSingle(InspectReportContentBo bo);

	int updateEditDate(long id);

	List<InspectReportContentBo> loadItemTwo(long catalogId);
}
