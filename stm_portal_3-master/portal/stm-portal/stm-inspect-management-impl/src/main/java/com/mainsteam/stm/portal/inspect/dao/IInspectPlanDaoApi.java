package com.mainsteam.stm.portal.inspect.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.inspect.bo.BasicInfoBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanClob;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanResultSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanSelfItemBo;
import com.mainsteam.stm.portal.inspect.bo.Routine;

public interface IInspectPlanDaoApi {

	Page<InspectPlanBasicBo, InspectPlanBasicBo> list(
			Page<InspectPlanBasicBo, InspectPlanBasicBo> page);

	int saveBasic(long id, String name, int type, String format, String domain,
			long inspector, String description, boolean reportChange,
			long creator);

	int updateBasic(long id, String name, int type, String format,
			String domain, long inspector, String description,
			boolean reportChange);

	int updateState(long id, boolean state);

	int updateRoutine(long id, boolean reportProduceTimeShow,
			boolean reportModifyTimeShow, boolean reportModifiorShow,
			boolean resourceShow, boolean businessShow, String resourceName,
			String businessName, String inspectReportResourceType);

	int addSelfItem(InspectPlanSelfItemBo bo);

	int addSelfItems(List<InspectPlanSelfItemBo> data);

	int delSelfItems(long basicId);

	int addInspectionItems(List<InspectPlanContentBo> data);

	int updateInspectionItem(InspectPlanContentBo data);

	int delInspectionItem(Long[] inspectionItemids);
	
	int delInspectionItemParent(Long[] inspectionItemids);

	List<Long> queryInspectionItemByThreeId(Long[] delId);

	List<InspectPlanContentBo> loadInspectionItems(long id);

	Routine getRoutine(long id);

	int saveConclusions(List<InspectPlanResultSettingBo> data);

	int delConclusionsByBasicId(long basicId);

	List<InspectPlanResultSettingBo> getConclusionsByBasicId(long basicId);

	/**
	 * 加载巡检计划基本信息
	 *
	 * @param id
	 * @return
	 */
	BasicInfoBo loadBasic(long id);

	List<InspectPlanContentBo> loadItem(long catalogId);

	int addItems(List<InspectPlanContentBo> data);

	int updateItem(InspectPlanContentBo data);

	int updateItemBasic(long id, String prefix, String subfix, String unit,
			String value, String describle);

	InspectPlanClob loadJobInfo(long id);

	int updateExecTime(long id, Date date);

	Map<String, Object> getPlan(long id);

	List<InspectPlanSelfItemBo> getSelfItem(long planId);

	boolean getState(long id);

	int del(long[] planIds);

	int delSelf(long[] planIds);

	int delResult(long[] planIds);

	Long[] getThreeItems(long[] planIds);

	Long[] getThreeItemsByItemIds(long[] itemIds);

	int copyPlan(long planId, long newId, long userId);

	int updateReportName(long planId, String name);
	
	Long selectReportByName(String name);

	Long[] getTwoItems(long[] parentIds);

	int updateEditDate(long id);

	List<Long> getContentByResourceId(long id);

	int delContent(List<Long> ids);

	List<InspectPlanContentBo> loadItemHierarchyTwo(long catalogId);
	int saveDomainBasic(Long id, long inspectid, long domainid);

	int del(long id, List<Long> ids);
	int delAll(long[] ids);
	List<InspectPlanBasicBo> inspectPlanloadAll(long id);
	InspectPlanBasicBo get(long inspectid , long domainid);
}
