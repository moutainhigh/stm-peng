package com.mainsteam.stm.portal.inspect.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.SchedulerException;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.inspect.bo.BasicInfoBo;
import com.mainsteam.stm.portal.inspect.bo.InspectDomainRole;
import com.mainsteam.stm.portal.inspect.bo.InspectFrontReportOrPlanBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanResultSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanSelfItemBo;
import com.mainsteam.stm.portal.inspect.bo.ReportResourceMetric;
import com.mainsteam.stm.portal.inspect.bo.Routine;

public interface IInspectPlanApi {

	Page<InspectPlanBasicBo, InspectPlanBasicBo> list(
			Page<InspectPlanBasicBo, InspectPlanBasicBo> page);

	long saveBasic(String name, int type, String format, String domain,
			long inspector, String description, boolean reportChange,
			long creator);

	int updateBasic(long id, String name, int type, String format,
			String domain, long inspector, String description,
			boolean reportChange);

	int updateState(long id, boolean state);

	int updateRoutine(long id, boolean reportProduceTimeShow,
			boolean reportModifyTimeShow, boolean reportModifiorShow,
			boolean resourceShow, boolean businessShow, String resourceName,
			String businessName,String inspectReportResourceType,InspectFrontReportOrPlanBo requestBo);

	int updateInspectionItems(Long basicId, Long[] id,
			String[] inspectPlanItemName, String[] inspectPlanItemDescrible,
			Long[] delId);

	List<InspectPlanContentBo> loadInspectionItems(long id);

	BasicInfoBo loadBasic(long id);

	Routine getRoutine(long id);

	int updateConclusion(long id,InspectFrontReportOrPlanBo requestBo);

	List<InspectPlanResultSettingBo> getConclusionsByBasicId(long basicId);

	List<InspectPlanContentBo> loadItem(long catalogId);

	int updateItem(String json);

	int updateItemBasic(String json);

	int updateItemChild(String json);

	int updateExecTime(long id, Date date);

	Map<String, Object> get(long id);

	List<InspectPlanSelfItemBo> getSelfItem(long planId);

	boolean saveExec(long id);

	/**
	 * 根据资源ID查询指标列表
	 */
	public List<ReportResourceMetric> getMetricListByResource(
			String[] resourceIdList);

	int delPlan(long[] ids) throws ClassNotFoundException, SchedulerException;

	int delItems(long[] itemIds);

	/**
	 * 复制
	 * 
	 * @param id
	 * @return 返回新生成的id
	 */
	long saveCopy(long id, long userId);

	int updateReportName(long planId, String name);

	int updateItems(long dirId, String json);

	List<InspectPlanContentBo> loadItemHierarchyTwo(long catalogId);
	int saveBasic(long inspectid , long domainid) ;


	List<InspectPlanBasicBo> inspectPlanloadAll(long id);
	InspectPlanBasicBo get(long inspectid , long domainid);

	int delItems(Long id,List<Long> items);
	
	List<InspectDomainRole> getDomainRoleByUserId(Long id,Long planId);
}
