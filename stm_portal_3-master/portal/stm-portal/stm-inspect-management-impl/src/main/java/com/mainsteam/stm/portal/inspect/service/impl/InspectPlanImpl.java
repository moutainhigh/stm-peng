package com.mainsteam.stm.portal.inspect.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.job.CronExpressionHelper;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.service.bo.BizBandwidthCapacityMetric;
import com.mainsteam.stm.portal.business.service.bo.BizCalculateCapacityMetric;
import com.mainsteam.stm.portal.business.service.bo.BizDatabaseCapacityMetric;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.portal.business.service.bo.BizMainDataBo;
import com.mainsteam.stm.portal.business.service.bo.BizMetricHistoryDataBo;
import com.mainsteam.stm.portal.business.service.bo.BizStoreCapacityMetric;
import com.mainsteam.stm.portal.business.service.service.util.BizMetricDefine;
import com.mainsteam.stm.portal.inspect.api.IInspectPlanApi;
import com.mainsteam.stm.portal.inspect.api.IInspectReportApi;
import com.mainsteam.stm.portal.inspect.bo.BasicInfoBo;
import com.mainsteam.stm.portal.inspect.bo.BasicTypeMultiplyBo;
import com.mainsteam.stm.portal.inspect.bo.InspectDomainRole;
import com.mainsteam.stm.portal.inspect.bo.InspectFrontReportOrPlanBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanClob;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanResultSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanSelfItemBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportResultsSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportSelfItemBo;
import com.mainsteam.stm.portal.inspect.bo.ReportResourceMetric;
import com.mainsteam.stm.portal.inspect.bo.Routine;
import com.mainsteam.stm.portal.inspect.dao.IInspectPlanDaoApi;
import com.mainsteam.stm.portal.inspect.job.PlanJob;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.bo.ResourceCategoryTree;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.DomainRole;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.StringUtil;
import com.mainsteam.stm.util.Util;

public class InspectPlanImpl implements IInspectPlanApi {

	private static final String JOB_PLAN = "PLAN_";
	private static final Logger logger = LoggerFactory
			.getLogger(IInspectPlanApi.class);

	private IInspectPlanDaoApi inspectPlanDao;
	

	private ISequence sequence;

	private IUserApi userApi;

	private ScheduleManager scheduleManager;

	private IDomainApi domainApi;
	
	private BizMainApi bizMainApi;

	private IInspectReportApi inspectReportApi;

	private MetricDataService metricDataService;

	private ReportTemplateApi reportTemplateApi;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;

	@Resource
	private CapacityService capacityService;

	@Autowired
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private CustomMetricService customMetricService;
	@Resource
	private MetricStateService metricStateService;
	@Resource
	private ProfileService profileService;
	/**
	 * 巡检倍数
	 */
	@Value("${stm.insplanNum}")
	private int insplanNum;
	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setReportTemplateApi(ReportTemplateApi reportTemplateApi) {
		this.reportTemplateApi = reportTemplateApi;
	}

	public void setMetricDataService(MetricDataService metricDataService) {
		this.metricDataService = metricDataService;
	}

	public void setInspectReportApi(IInspectReportApi inspectReportApi) {
		this.inspectReportApi = inspectReportApi;
	}
 
	public BizMainApi getBizMainApi() {
		return bizMainApi;
	}

	public void setBizMainApi(BizMainApi bizMainApi) {
		this.bizMainApi = bizMainApi;
	}

	public void setDomainApi(IDomainApi domainApi) {
		this.domainApi = domainApi;
	}

	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}

	public void setUserApi(IUserApi userApi) {
		this.userApi = userApi;
	}

	public void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	public IInspectPlanDaoApi getInspectPlanDao() {
		return inspectPlanDao;
	}

	public void setInspectPlanDao(IInspectPlanDaoApi inspectPlanDao) {
		this.inspectPlanDao = inspectPlanDao;
	}
	@Override
	public Page<InspectPlanBasicBo, InspectPlanBasicBo> list(
			Page<InspectPlanBasicBo, InspectPlanBasicBo> page) {
		this.inspectPlanDao.list(page);
		if (page.getDatas() != null && page.getDatas().size() > 0) {
			Map<Long, User> map = this.userApi.getAll();
			List<Domain> domains = this.domainApi.getAllDomains();
			Map<Long, Domain> domainsMap = new HashMap<>();
			if (domains != null) {
				for (Domain d : domains) {
					domainsMap.put(d.getId(), d);
				}
			}
			for (InspectPlanBasicBo bo : page.getDatas()) {
				if (bo.getInspectPlanCreator() != null) {
					User u = map.get(bo.getInspectPlanCreator());
					bo.setCreateUserName(u != null ? u.getName() : "");
				}
				if (bo.getInspectPlanInspector() != null) {
					User u = map.get(bo.getInspectPlanInspector());
					bo.setInspectorName(u == null ? "" : u.getName());
				}
				if (bo.getInspectPlanDomain() != null) {
					String[] domainIds = bo.getInspectPlanDomain().split(",");
					StringBuffer domainName = new StringBuffer();
					if(domainIds.length != 0){  
						Long id = null;
						int  num = 1;
						Domain domain = null; 
						
						for (String domainId : domainIds) {
							id = Long.parseLong(domainId);
							if(domainsMap.containsKey(id)){
								domain = domainsMap.get(id);  
								if(num > 1){
								   domainName.append(",");
								} 
								domainName.append(domain.getName());
								num ++;
							}
							
						}
					}
					bo.setDomainName(domainName.toString());
				}
			}
		}
		return page;
	}

	@Override
	public long saveBasic(String name, int type, String format, String domain,
			long inspector, String description, boolean reportChange,
			long creator) {
		Long basicNum = inspectPlanDao.selectReportByName(name);
		if(basicNum > 0){
			return 1l;
		}
		long id = this.sequence.next();
		this.inspectPlanDao.saveBasic(id, name, type, format, domain,
				inspector, description, reportChange, creator);
		saveTemplate(id);
		String[] domains=domain.split(",");
		if(domains.length!=0){
			for (String did : domains) {
				logger.info(id +" -----" + Long.parseLong(did));
				Long idSeq=this.sequence.next();
				inspectPlanDao.saveDomainBasic(idSeq,id, Long.parseLong(did));
			}
		}
		return id;
	}

	private void saveTemplate(long id) {
		List<InspectPlanContentBo> contensData = new ArrayList<InspectPlanContentBo>();

		InspectPlanContentBo cRoot = new InspectPlanContentBo();
		contensData.add(cRoot);
		cRoot.setId(this.sequence.next());
		cRoot.setInspectPlanId(id);
		cRoot.setInspectPlanItemName("机房环境");
		cRoot.setInspectPlanItemDescrible("检查机房环境相关项");
		InspectPlanContentBo c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("温度");
		c.setInspectPlanItemDescrible("机房温度,预置检查项");
		c.setIndicatorAsItem(true);
		c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("湿度");
		c.setInspectPlanItemDescrible("机房湿度,预置检查项");
		c.setIndicatorAsItem(true);
		c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("痕迹");
		c.setInspectPlanItemDescrible("机房是否有异常痕迹,预置检查项");
		c.setIndicatorAsItem(true);
		c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("清洁");
		c.setInspectPlanItemDescrible("机房清洁状况,预置检查项，是否清洁");
		c.setIndicatorAsItem(true);
		c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("异响");
		c.setInspectPlanItemDescrible("机房是否有异常响动,预置检查项");
		c.setIndicatorAsItem(true);
		c = new InspectPlanContentBo();
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("异味");
		c.setInspectPlanItemDescrible("机房是否有异常味道,预置检查项");
		c.setIndicatorAsItem(true);

		cRoot = new InspectPlanContentBo();
		contensData.add(cRoot);
		cRoot.setId(this.sequence.next());
		cRoot.setInspectPlanId(id);
		cRoot.setInspectPlanItemName("周边设备");
		cRoot.setInspectPlanItemDescrible("检查机房配套设施");
		c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("UPS");
		c.setInspectPlanItemDescrible("UPS是否正常");
		c.setIndicatorAsItem(true);
		c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("电源");
		c.setInspectPlanItemDescrible("电源是否正常");
		c.setIndicatorAsItem(true);
		c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("空调");
		c.setInspectPlanItemDescrible("空调是否正常");
		c.setIndicatorAsItem(true);
		c = new InspectPlanContentBo();
		contensData.add(c);
		c.setId(this.sequence.next());
		c.setInspectPlanParentId(cRoot.getId());
		c.setInspectPlanItemName("消防");
		c.setInspectPlanItemDescrible("消防安全是否正常");
		c.setIndicatorAsItem(true);

		cRoot = new InspectPlanContentBo();
		contensData.add(cRoot);
		cRoot.setId(this.sequence.next());
		cRoot.setInspectPlanId(id);
		cRoot.setInspectPlanItemName("网络设备");
		cRoot.setInspectPlanItemDescrible("检查机房内网络设施健康情况");

		cRoot = new InspectPlanContentBo();
		contensData.add(cRoot);
		cRoot.setId(this.sequence.next());
		cRoot.setInspectPlanId(id);
		cRoot.setInspectPlanItemName("服务器");
		cRoot.setInspectPlanItemDescrible("检查机房内服务器健康情况");

		cRoot = new InspectPlanContentBo();
		contensData.add(cRoot);
		cRoot.setId(this.sequence.next());
		cRoot.setInspectPlanId(id);
		cRoot.setInspectPlanItemName("业务系统");
		cRoot.setInspectPlanItemDescrible("检查业务系统运行情况");

		this.inspectPlanDao.addItems(contensData);
	}

	@Override
	public int updateState(long id, boolean state) {
		int u = this.inspectPlanDao.updateState(id, state);
		task(id, state, u);
		return u;
	}

	private void task(long id, boolean state, int u) {
		if (u > 0) {
			String jobKey = JOB_PLAN + id;
			if (state) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", id);
				InspectPlanClob jobMap = this.inspectPlanDao
						.loadJobInfo(id);
				if (jobMap != null) {
				//	Integer type =Integer.parseInt(String.valueOf(jobMap.get("TYPE"))); // (Integer) jobMap.get("TYPE");
					Integer type=null;
					String timeStr=null;
					if(/*jobMap.get("type")*/(Integer)jobMap.getType()!=null ){
						type=	(Integer)jobMap.getType()/*(Integer) jobMap.get("type")*/;
					}else{
						type =(Integer)jobMap.getType()/*String.valueOf(jobMap.get("TYPE"))*/;
					}
					if(/*jobMap.get("timeStr")*/jobMap.getTimeStr()!=null){
						timeStr = jobMap.getTimeStr()/*jobMap.get("timeStr").toString()*/;
					}else{
					//	timeStr = (String) jobMap.get("TIMESTR");
						try {
							
							timeStr=jobMap.getTimeStr();//ClobToString((Clob)jobMap.get);
						} catch (Exception e) {
							e.printStackTrace();
						}/* catch (Exception e) {
							e.printStackTrace();
						}*/
					}
					
				//	String timeStr = (String) jobMap.get("timeStr");
					if (type != null && timeStr != null
							&& !"".equals(timeStr.trim()) ) {
						Set<String> cronExpressions = new HashSet<>();
						logger.info("timeStr是！！！！！！！！"+timeStr);
						System.out.println(timeStr);
						JSONArray array = JSONArray.parseArray(timeStr);
						
						switch (type) {
						case 1:
							// 不需要执行什么，留作备用
							break;
						case 2:
							for (int i = 0; i < array.size(); i++) {
								JSONObject obj = array.getJSONObject(i);
								CronExpressionHelper ce = new CronExpressionHelper();
								ce.set(CronExpressionHelper.SECOND, "00")
										.set(CronExpressionHelper.MINUTE,
												obj.getString("minute"))
										.set(CronExpressionHelper.HOUR,
												obj.getString("hour"));
								cronExpressions.add(ce.toString());
							}
							break;
						case 3:
							for (int i = 0; i < array.size(); i++) {
								JSONObject obj = array.getJSONObject(i);
								CronExpressionHelper ce = new CronExpressionHelper();
								ce.set(CronExpressionHelper.SECOND, "00")
										.set(CronExpressionHelper.MINUTE,
												obj.getString("minute"))
										.set(CronExpressionHelper.HOUR,
												obj.getString("hour"))
										.set(CronExpressionHelper.WEEK,
												obj.getString("week"));
								cronExpressions.add(ce.toString());
							}
							break;
						case 4:
							for (int i = 0; i < array.size(); i++) {
								JSONObject obj = array.getJSONObject(i);
								CronExpressionHelper ce = new CronExpressionHelper();
								ce.set(CronExpressionHelper.SECOND, "00")
										.set(CronExpressionHelper.MINUTE,
												obj.getString("minute"))
										.set(CronExpressionHelper.HOUR,
												obj.getString("hour"));
								String day = obj.getString("day");
								if (day.indexOf("L") > 0) {
									ce.set(CronExpressionHelper.DAY, "L");
									cronExpressions.add(ce.toString());
									day = day.replaceAll(",L", "");
								}
								ce.set(CronExpressionHelper.DAY, day);
								cronExpressions.add(ce.toString());
							}
							break;
						case 5:
							for (int i = 0; i < array.size(); i++) {
								JSONObject obj = array.getJSONObject(i);
								CronExpressionHelper ce = new CronExpressionHelper();
								ce.set(CronExpressionHelper.SECOND, "00")
										.set(CronExpressionHelper.MINUTE,
												obj.getString("minute"))
										.set(CronExpressionHelper.HOUR,
												obj.getString("hour"))
										.set(CronExpressionHelper.DAY,
												obj.getString("day"))
										.set(CronExpressionHelper.MONTH,
												obj.getString("month"))
										.set(CronExpressionHelper.YEAR,
												obj.getString("year"));
								cronExpressions.add(ce.toString());
							}
							break;
						}
						IJob job = new IJob(jobKey, new PlanJob(),
								cronExpressions, map);
						try {
							this.scheduleManager.updateJob(jobKey, job);
						} catch (ClassNotFoundException | SchedulerException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				try {
					this.scheduleManager.deleteJob(jobKey);
				} catch (ClassNotFoundException | SchedulerException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int updateBasic(long id, String name, int type, String format,
			String domain, long inspector, String description,
			boolean reportChange) {
		BasicInfoBo baseic = this.inspectPlanDao.loadBasic(id);
		int u = this.inspectPlanDao.updateBasic(id, name, type, format, domain,
				inspector, description, reportChange);
		if (inspector!=Long.parseLong(baseic.getInspectPlanInspector())) {//根据巡检人变化判断是否删除历史巡检计划内容
			List<Long> ids = this.inspectPlanDao.getContentByResourceId(id);
			if (ids != null && ids.size() > 0) {
				this.inspectPlanDao.delContent(ids);
			}
		}
		if (u > 0) {
			this.inspectPlanDao.updateEditDate(id);
			if (this.inspectPlanDao.getState(id)) {
				String[] domains=domain.split(",");
				if(domains.length!=0){
					List<Long> ids= new ArrayList<Long>();
					for (int i=0;i<domains.length;i++) {
					InspectPlanBasicBo bo=	inspectPlanDao.get(id, Long.parseLong(domains[i]));
					if(bo!=null){
						ids.add(Long.parseLong(domains[i]));
					}else{
						Long idSeq=this.sequence.next();
						inspectPlanDao.saveDomainBasic(idSeq,id, Long.parseLong(domains[i]));
						ids.add(Long.parseLong(domains[i]));
					}
						
					}
					if(ids!=null && ids.size()!=0){
						inspectPlanDao.del(id,ids);
					}
				}
				
				this.task(id, true, u);
			}
		}
	
		return u;
	}

	@Override
	public int updateRoutine(long id, boolean reportProduceTimeShow,
			boolean reportModifyTimeShow, boolean reportModifiorShow,
			boolean resourceShow, boolean businessShow, String resourceName,
			String businessName, String inspectReportResourceType,InspectFrontReportOrPlanBo requestBo) {
		this.inspectPlanDao.updateRoutine(id, reportProduceTimeShow,
				reportModifyTimeShow, reportModifiorShow, resourceShow,
				businessShow, resourceName, businessName,
				inspectReportResourceType);
		if (resourceShow) {
			Routine r = this.inspectPlanDao.getRoutine(id);
			if (resourceName != null
					&& (r.getResourceName() == null || !resourceName.equals(r
							.getResourceName()))) {
				List<Long> ids = this.inspectPlanDao.getContentByResourceId(id);
				if (ids != null && ids.size() > 0) {
					this.inspectPlanDao.delContent(ids);
				}
			}
		}
		this.inspectPlanDao.delSelfItems(id);
		//modify by sunhailiang on 20170615 
		if(requestBo != null){ 
			List<InspectPlanSelfItemBo> list = requestBo.getPlanSelfItemsList();
			if(list != null && !list.isEmpty()){
				List<InspectPlanSelfItemBo> itemList = new ArrayList<InspectPlanSelfItemBo>();
				String itemName = null;
				for(InspectPlanSelfItemBo itemBo : list){
					itemName = itemBo.getInspectPlanSelfItemName();
					if(itemName != null && !"".equals(itemName)){
						InspectPlanSelfItemBo newBo = getInspectPlanSelfItemBo(itemBo);
						newBo.setId(this.sequence.next());
						newBo.setInspectPlanId(id);
						itemList.add(newBo);
					}
				}
				if(!itemList.isEmpty()){
					this.inspectPlanDao.addSelfItems(itemList);	
				}
			}
		}
		
		this.inspectPlanDao.updateEditDate(id);
		return 1;
	}
	
	private InspectPlanSelfItemBo getInspectPlanSelfItemBo(InspectPlanSelfItemBo bo){
		InspectPlanSelfItemBo returnBo = new InspectPlanSelfItemBo();
		returnBo.setInspectPlanSelfItemName(bo.getInspectPlanSelfItemName());
		returnBo.setInspectPlanSelfItemType(bo.getInspectPlanSelfItemType());
		returnBo.setInspectPlanItemContent(bo.getInspectPlanItemContent()); 
		return returnBo;
	}

	@Override
	public int updateInspectionItems(Long basicId, Long[] id,
			String[] inspectPlanItemName, String[] inspectPlanItemDescrible,
			Long[] delId) {
		if (id != null && id.length > 0) {
			List<InspectPlanContentBo> adds = new ArrayList<InspectPlanContentBo>();
			for (int i = 0; i < id.length; i++) {
				InspectPlanContentBo bo = new InspectPlanContentBo();
				bo.setInspectPlanItemName(inspectPlanItemName[i]);
				bo.setInspectPlanItemDescrible(inspectPlanItemDescrible.length > i ? inspectPlanItemDescrible[i]
						: "");
				bo.setSort(i + 1);
				if (id[i] == null || id[i] == -1) {
					bo.setId(this.sequence.next());
					bo.setInspectPlanId(basicId);
					adds.add(bo);
				} else {
					bo.setId(id[i]);
					this.inspectPlanDao.updateInspectionItem(bo);
				}
			}
			if (adds.size() > 0) {
				this.inspectPlanDao.addInspectionItems(adds);
			}
		}
		if (delId != null && delId.length > 0) {
			List<Long> ids = this.inspectPlanDao
					.queryInspectionItemByThreeId(delId);// 查询要删除的根id的第三级id
			if (ids != null && ids.size() > 0) {
				Long[] threeIds = new Long[ids.size()];
				ids.toArray(threeIds);
				this.inspectPlanDao.delInspectionItem(threeIds);
			}
			this.inspectPlanDao.delInspectionItem(delId);// 删除第一级和第二级的id
		}
		this.inspectPlanDao.updateEditDate(basicId);
		return 1;
	}

	@Override
	public List<InspectPlanContentBo> loadInspectionItems(long id) {
		return this.inspectPlanDao.loadInspectionItems(id);
	}

	/**
	 * 加载巡检计划基本信息
	 */
	@Override
	public BasicInfoBo loadBasic(long id) {
		
		BasicInfoBo ret = this.inspectPlanDao.loadBasic(id);
		if(ret.getInspectPlanDomain()!="" && ret.getInspectPlanDomain()!=null){
			String[] domainIds=ret.getInspectPlanDomain().split(",");
			Long[] domainLongIds=new Long[domainIds.length];
			if(domainIds.length!=0){
				for (int i=0;i<domainIds.length;i++) {
					domainLongIds[i]=Long.parseLong(domainIds[i]);
				}
				String nameTemp=null;
				if(domainIds.length!=0){
					for (int i=0;i<domainIds.length;i++) {
						Domain momain = domainApi.get(Long.parseLong(domainIds[i]));
						if(i==0){
							nameTemp=momain.getName()+",";
						}else{
							nameTemp+=momain.getName()+",";
						}
					}
				}
//				Domain momain = domainApi.get(Long.parseLong(dom));
				String name=nameTemp.substring(0,nameTemp.length()-1);
				if (name != null) {
					ret.setInspectPlanDomainShowname(name);
				}
			}
		}
		
	if (null != ret.getInspectRlanReportEditable()
			&& 1 == Integer.parseInt(ret.getInspectRlanReportEditable())) {
		ret.setInspectRlanReportEditable("on");
	}
	if (null != ret.getInspectPlanTypeTime()) {
		JSONArray jsonArray = JSONObject.parseArray(ret
				.getInspectPlanTypeTime());
		if (StringUtils.isNotEmpty(ret.getInspectPlanType())) {
			String type = ret.getInspectPlanType();
			if ("1".equals(type)) { // 手动
			} else if ("2".equals(type)) { // 每天
				JSONObject json = jsonArray.getJSONObject(0);
				String hour = "", minute = "";
				if (StringUtils.isNotEmpty(json.getString("hour"))) {
					hour = json.getString("hour");
				}
				if (StringUtils.isNotEmpty(json.getString("minute"))) {
					minute = json.getString("minute");
				}
				ret.setInspectTypeHour(hour);
				ret.setInspectTypeMinute(minute);
			} else if ("3".equals(type)) { // 每周
				JSONObject json = jsonArray.getJSONObject(0);
				if (StringUtils.isNotEmpty(json.getString("week"))) {
					String week = "", hour = "", minute = "";
					week = json.getString("week");
					String[] weeks = week.split(",");
					for (String w : weeks) {
						if (null == w || "".equals(w) || 0 == w.length())
							continue;
						switch (w) {
						case "MON":
							ret.setInspectTypeMonday("on");
							break;
						case "TUE":
							ret.setInspectTypeTuesday("on");
							break;
						case "WED":
							ret.setInspectTypeWednesday("on");
							break;
						case "THU":
							ret.setInspectTypeThursday("on");
							break;
						case "FRI":
							ret.setInspectTypeFriday("on");
							break;
						case "SAT":
							ret.setInspectTypeSaturday("on");
							break;
						case "SUN":
							ret.setInspectTypeSunday("on");
							break;
						}
					}
					if (StringUtils.isNotEmpty(json.getString("hour"))) {
						hour = json.getString("hour");
					}
					if (StringUtils.isNotEmpty(json.getString("minute"))) {
						minute = json.getString("minute");
					}
					ret.setInspectTypeHour(hour);
					ret.setInspectTypeMinute(minute);
				}
			} else if ("4".equals(type)) { // 每月
				List<BasicTypeMultiplyBo> lines = new ArrayList<BasicTypeMultiplyBo>();
				for (int i = 0, len = jsonArray.size(); i < len; i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					String day = "", hour = "", minute = "";
					List<String> dates = new ArrayList<>();
					if (StringUtils.isNotEmpty(json.getString("day"))) {
						day = json.getString("day");
						if (null != day && !"".equals(day)
								&& 0 != day.length()) {
							String[] days = day.trim().split(",");
							for (String s : days) {
								if (null != s && !"".equals(s.trim()))
									dates.add(s);
							}
						}
					}
					if (StringUtils.isNotEmpty(json.getString("hour"))) {
						hour = json.getString("hour");
					}
					if (StringUtils.isNotEmpty(json.getString("minute"))) {
						minute = json.getString("minute");
					}
					BasicTypeMultiplyBo line = new BasicTypeMultiplyBo(
							dates, hour, minute);
					lines.add(line);
				}
				ret.setLines(lines);
			} else if ("5".equals(type)) { // 自定义
				List<BasicTypeMultiplyBo> lines = new ArrayList<BasicTypeMultiplyBo>();
				String[] dates = new String[jsonArray.size()];
				for (int i = 0, len = jsonArray.size(); i < len; i++) {
					JSONObject json = (JSONObject) jsonArray.get(i);
					dates[i] = json.getString("year") + "-"
							+ json.getString("month") + "-"
							+ json.getString("day") + " "
							+ json.getString("hour") + ":"
							+ json.getString("minute");
				}
				BasicTypeMultiplyBo line = new BasicTypeMultiplyBo();
				lines.add(line);
				line.setCustomDate(dates);
				ret.setLines(lines);
			}
		}
	}
	return ret;}

	@Override
	public Routine getRoutine(long id) {
		Routine r = this.inspectPlanDao.getRoutine(id);
		if (r != null) {
			if (!StringUtil.isNull(r.getInspectReportResourceName())) {
				List<ResourceCategoryTree> rs = this.reportTemplateApi
						.getAllResourceCategory();
				if (rs != null && rs.size() > 0) {
					for (ResourceCategoryTree re : rs) {
						if (r.getInspectReportResourceName().equals(re.getId())) {
							r.setResourceName(re.getName());
							break;
						}
					}
				}
			}
			if (!StringUtil.isNull(r.getInspectReportBusinessName())) {
				BizMainBo biz =	bizMainApi.getBasicInfo(Long.parseLong(r
						.getInspectReportBusinessName()));
			/*	BizServiceBo biz = this.bizServiceApi.get(Long.parseLong(r
						.getInspectReportBusinessName()));*/
				if (biz != null) {
					r.setBusinessName(biz.getName());
				}
			}
			if (r.getInspector() != null) {
				User u = this.userApi.get(r.getInspector());
				if (u != null) {
					r.setInspectorName(u.getName());
				}
			}
		}
		return r;
	}

	@Override
	public int updateConclusion(long id,InspectFrontReportOrPlanBo requestBo){
		this.inspectPlanDao.delConclusionsByBasicId(id);
		//add by sunhailiang on 20170615
	    if(requestBo != null){
	    	List<InspectPlanResultSettingBo> planList = requestBo.getPlanResultsList();
	    	if(planList != null && !planList.isEmpty()){
	    		List<InspectPlanResultSettingBo> list = new ArrayList<InspectPlanResultSettingBo>();
	    		for(InspectPlanResultSettingBo bo : planList){
	    			InspectPlanResultSettingBo returnBo = getInspectPlanResultSettingBo(bo);
	    			returnBo.setId(this.sequence.next());
	    			returnBo.setInspectPlanId(id);
	    			list.add(returnBo);
	    		}
	    		
	    		if(!list.isEmpty()){
	    			this.inspectPlanDao.saveConclusions(list);
	    		}
	    	}
	    }
		 
		this.inspectPlanDao.updateEditDate(id);
		return 1;
	}
   
	private InspectPlanResultSettingBo getInspectPlanResultSettingBo(InspectPlanResultSettingBo bo){
		InspectPlanResultSettingBo returnBo = new InspectPlanResultSettingBo();
		returnBo.setInspectPlanSummeriseName(bo.getInspectPlanSummeriseName());
		returnBo.setInspectPlanSumeriseDescrible(bo.getInspectPlanSumeriseDescrible());
		return returnBo;
	}
	
	@Override
	public List<InspectPlanResultSettingBo> getConclusionsByBasicId(long basicId) {
		return this.inspectPlanDao.getConclusionsByBasicId(basicId);
	}

	@Override
	public List<InspectPlanContentBo> loadItem(long catalogId) {
		return this.inspectPlanDao.loadItem(catalogId);
	}

	@Override
	public int updateItem(String json) {
		JSONObject jsonObject = JSONObject.parseObject(json);
		// long planId = jsonObject.getLong("inspectPlanId");
		long dirId = jsonObject.getLong("dirId");
		JSONArray jsonArray = jsonObject.getJSONArray("inspectItems");
		if (jsonObject.containsKey("delId")) {
			String delIdStr = jsonObject.getString("delId");
			if (delIdStr != null && !"".equals(delIdStr.trim())) {
				String[] delIdArray = delIdStr.split(",");
				Long[] delIds = new Long[delIdArray.length];
				for (int i = 0; i < delIdArray.length; i++) {
					delIds[i] = Long.parseLong(delIdArray[i]);
				}
				this.inspectPlanDao.delInspectionItem(delIds);
			}
		}
		if (jsonArray != null && jsonArray.size() > 0) {
			List<InspectPlanContentBo> data = new ArrayList<>();
			List<InspectPlanContentBo> updateData = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				String name = obj.getString("name");
				String desc = obj.getString("desc");
				InspectPlanContentBo c = new InspectPlanContentBo();
				c.setInspectPlanItemName(name);
				c.setInspectPlanItemDescrible(desc);
				c.setSort(i + 1);
				if (obj.containsKey("id")) {
					Long id = obj.getLong("id");
					if (id != null && id != -1) {
						c.setId(id);
						updateData.add(c);
						continue;
					}
				}
				data.add(c);
				c.setInspectPlanParentId(dirId);
				c.setId(sequence.next());
				int type = obj.getInteger("type");
				switch (type) {
				case 1:
					c.setEdit(true);
					c.setIndicatorAsItem(true);
					break;
				case 2:
					long resouceId = obj.getLong("resouceid");
					JSONArray points = obj.getJSONArray("points");
					if (points != null && points.size() > 0) {
						c.setChildren(new ArrayList<InspectPlanContentBo>());
						c.setIndicatorAsItem(true);
						c.setResourceId(resouceId);
						c.setModelId(obj.getString("modelid"));
						for (int j = 0; j < points.size(); j++) {
							JSONObject p = points.getJSONObject(j);
							String indexModelId = p.getString("pointid");
							String indexModelUnit = p.getString("unit");
							String indexModelName = p.getString("name");
							String indexModelDesc = p.getString("desc");
							InspectPlanContentBo indexModel = new InspectPlanContentBo();
							data.add(indexModel);
							indexModel.setId(sequence.next());
							indexModel.setInspectPlanParentId(c.getId());
							indexModel.setIndexModelId(indexModelId);
							indexModel.setResourceId(resouceId);
							indexModel.setInspectPlanItemUnit(indexModelUnit);
							indexModel.setInspectPlanItemName(indexModelName);
							indexModel
									.setInspectPlanItemDescrible(indexModelDesc);
							indexModel.setModelId(c.getModelId());
							indexModel.setEdit(true);
						}
					}
					break;
				case 3:
					long resouceId3 = obj.getLong("resouceid");
					String indexModelId = obj.getString("pointid");
					String indexModelUnit = obj.getString("unit");
					String modelId = obj.getString("modelid");
					c.setResourceId(resouceId3);
					c.setIndexModelId(indexModelId);
					c.setInspectPlanItemUnit(indexModelUnit);
					c.setModelId(modelId);
					c.setEdit(true);
					c.setIndicatorAsItem(true);
					break;
				}
			}
			if (updateData.size() > 0) {
				for (InspectPlanContentBo inspectPlanContentBo : updateData) {
					this.inspectPlanDao.updateItem(inspectPlanContentBo);
				}
			}
			if (data.size() > 0) {
				this.inspectPlanDao.addItems(data);
			}
		}
		return 1;
	}

	@Override
	public int updateItemBasic(String json) {
		JSONArray array = JSONArray.parseArray(json);
		if (array != null && array.size() > 0) {
			for (int i = 0; i < array.size(); i++) {
				this.updateItemBasic(array.getJSONObject(i));
			}
		}
		return 1;
	}

	private void updateItemBasic(JSONObject obj) {
		long id = obj.getLong("id");
		String prefix = obj.getString("inspectPlanItemReferencePrefix");
		String subfix = obj.getString("inspectPlanItemReferenceSubfix");
		String unit = obj.getString("inspectPlanItemUnit");
		String value = obj.getString("inspectPlanItemValue");
		String describle = obj.getString("inspectPlanItemConditionDescrible");
		this.inspectPlanDao.updateItemBasic(id, prefix, subfix, unit, value,
				describle);
		if (obj.containsKey("children")) {
			JSONArray array = obj.getJSONArray("children");
			if (array != null && array.size() > 0) {
				for (int i = 0; i < array.size(); i++) {
					this.updateItemBasic(array.getJSONObject(i));
				}
			}
		}
	}

	@Override
	public int updateItemChild(String json) {
		JSONObject jsonObject = JSONObject.parseObject(json);
		if (jsonObject.containsKey("delId")) {
			String delIdStr = jsonObject.getString("delId");
			if (delIdStr != null && !"".equals(delIdStr.trim())) {
				String[] array = delIdStr.split(",");
				Long[] delIds = new Long[array.length];
				for (int i = 0; i < array.length; i++) {
					delIds[i] = Long.parseLong(array[i]);
				}
				this.inspectPlanDao.delInspectionItem(delIds);
			}
		}
		long itemId = jsonObject.getLong("itemId");
		JSONArray array = jsonObject.getJSONArray("inspectChildItems");
		if (array != null && array.size() > 0) {
			List<InspectPlanContentBo> data = new ArrayList<>();
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Long id = obj.getLong("id");
				String name = obj.getString("name");
				String desc = obj.getString("desc");
				InspectPlanContentBo c = new InspectPlanContentBo();
				c.setInspectPlanParentId(itemId);
				c.setInspectPlanItemName(name);
				c.setInspectPlanItemDescrible(desc);
				c.setSort(i + 1);
				if (id == null || id == -1) {
					if (obj.containsKey("pointid")) {
						c.setIndexModelId(obj.getString("pointid"));
					}
					if (obj.containsKey("resouceId")) {
						c.setResourceId(obj.getLong("resouceId"));
					}
					if (obj.containsKey("modelid")) {
						c.setModelId(obj.getString("modelid"));
					}
					if (obj.containsKey("unit")) {
						c.setInspectPlanItemUnit(obj.getString("unit"));
					}
					c.setId(sequence.next());
					c.setEdit(true);
					data.add(c);
				} else {
					c.setId(id);
					this.inspectPlanDao.updateItem(c);
				}
			}
			if (data.size() > 0) {
				this.inspectPlanDao.addItems(data);
			}
		}
		return 1;
	}

	@Override
	public int updateExecTime(long id, Date date) {
		return this.inspectPlanDao.updateExecTime(id, date);
	}

	@Override
	public Map<String, Object> get(long id) {
		return this.inspectPlanDao.getPlan(id);
	}

	@Override
	public List<InspectPlanSelfItemBo> getSelfItem(long planId) {
		return this.inspectPlanDao.getSelfItem(planId);
	}

	@Override
	public boolean saveExec(long id) {
		Map<String, Object> map = this.get(id);
		if (map != null && !map.isEmpty()) {
			InspectReportBasicBo data = new InspectReportBasicBo();
			data.setPlanId(id);
			Boolean isOracle = false;
			Object idLower = map.get("id");
			if (idLower == null) {
				isOracle = true;
			}
			logger.info("MAP的domain是##########"+map.get("DOMAIN").toString());
			String dom = map.get("DOMAIN").toString();
			if (dom != null && !"".equals(dom)) {
				String[] d=dom.split(",");
				String nameTemp=null;
				if(d.length!=0){
					for (int i=0;i<d.length;i++) {
						Domain momain = domainApi.get(Long.parseLong(d[i]));
						if(i==0){
							nameTemp=momain.getName()+",";
						}else{
							nameTemp+=momain.getName()+",";
						}
					}
				}
//				Domain momain = domainApi.get(Long.parseLong(dom));
				String name=nameTemp.substring(0,nameTemp.length()-1);
				if (name != null) {
					data.setInspectReportDomain(name);
				}
			}
			Object inspectorObj = map.get("INSPECTOR");
			if (inspectorObj != null) {
				Long inspector = Long.parseLong(inspectorObj.toString());
				logger.info("MAP的domain是##########"+inspector);
				data.setInspectorId(inspector);
				User user = userApi.get(inspector);
				if (user != null) {
					data.setInspectReportInspector(user.getName());
				}
			}
			if (isOracle) {
				data.setInspectReportProduceTimeShow(Integer.parseInt(map.get("REPORT_PRODUCE_TIME_SHOW").toString())
						==0 ? false : true);
				data.setInspectReportModifyTimeShow(Integer.parseInt(map
						.get("REPORT_MODIFY_TIME_SHOW").toString()) == 0 ? false : true);
				data.setInspectReportModifiorShow(Integer.parseInt(map
						.get("REPORT_MODIFIOR_SHOW").toString()) == 0 ? false : true);
			} else {
				data.setInspectReportProduceTimeShow(Boolean.parseBoolean(map.get("REPORT_PRODUCE_TIME_SHOW").toString()));
				data.setInspectReportModifyTimeShow(Boolean.parseBoolean(map.get("REPORT_MODIFY_TIME_SHOW").toString()));
				data.setInspectReportModifiorShow(Boolean.parseBoolean(map.get("REPORT_MODIFIOR_SHOW").toString()));
			}
			
			Object editObj = map.get("REPORT_EDITABLE");
			if (editObj != null) {
				Boolean edit = false;
				if (isOracle) {
					edit = Integer.parseInt(editObj.toString()) == 0 ? false : true;
				} else {
					edit = Boolean.parseBoolean(editObj.toString());
					logger.info("MAP的editObj是#######"+edit);
				}
				if (edit) {
					data.setEdit(true);
				} else {
					data.setEdit(false);
				}
			} else {
				data.setEdit(false);
			}
			
			Object resourceObj = map.get("RESOURCE_SHOW");
			if (resourceObj != null) {
				Boolean resource = false;
				if (isOracle) {
					resource = Integer.parseInt(resourceObj.toString()) == 0 ? false : true;
				} else {
					resource = Boolean.parseBoolean(resourceObj.toString());
					logger.info("MAP的resourceObj是#######"+resource);
				}
				if (resource) {
					String resourceId = map.get("RESOURCE_NAME").toString();
					if (resourceId != null && !"".equals(resourceId)) {
						List<ResourceCategoryTree> rs = this.reportTemplateApi
								.getAllResourceCategory();
						if (rs != null && rs.size() > 0) {
							for (ResourceCategoryTree r : rs) {
								if (resourceId.equals(r.getId())) {
									data.setInspectReportResourceName(r.getName());
									break;
								}
							}
						}
					}
				}
			}
			Object bussinessObj = map.get("BUSINESS_SHOW");
			if (bussinessObj != null) {
				Boolean business = false;
				if (isOracle) {
					business = Integer.parseInt(bussinessObj.toString()) == 0 ? false : true;
				}else {
					business = Boolean.parseBoolean(bussinessObj.toString());
					logger.info("MAP的business是#######"+business);
				}
				if (business) {
					String businessId = map.get("BUSINESS_NAME").toString();
					if (businessId != null && !"".equals(businessId)) {
						/*BizServiceBo biz = bizServiceApi.get(Long
								.parseLong(businessId));*/
						BizMainBo biz =	bizMainApi.getBasicInfo(Long.parseLong(businessId));
						if (biz != null) {
							data.setInspectReportBusinessName(biz.getName());
						}
					}
				}
			}
			data.setInspectReportStatus(false);
			Object createUserIdObj = map.get("CREATOR");
			if (createUserIdObj != null) {
				Long createUserId = Long.parseLong(createUserIdObj.toString());
				data.setCreatorId(createUserId);

				User u = userApi.get(createUserId);
				if (u != null) {
					data.setInspectReportTaskCreator(u.getName());
				}
			}
//			Long createUserId = (Long) map.get("CREATOR");
//			data.setCreatorId(createUserId);
//			if (createUserId != null) {
//				User u = userApi.get(createUserId);
//				if (u != null) {
//					data.setInspectReportTaskCreator(u.getName());
//				}
//			}
			data.setInspectReportPlanName((String) map.get("TASK_NAME"));
			String reportName = (String) map.get("REPORT_NAME");
			data.setInspectReportName(reportName == null ? data
					.getInspectReportPlanName() : reportName);
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			data.setInspectReportProduceTime(sdf.format(date));
			int u = inspectReportApi.saveBasic(data);
			logger.info("插入是否成功？？？？"+u);
			if (u > 0) {
				List<InspectPlanSelfItemBo> list = this.getSelfItem(id);
				if (list != null && list.size() > 0) {
					List<InspectReportSelfItemBo> reportSelfs = new ArrayList<>();
					for (InspectPlanSelfItemBo b : list) {
						InspectReportSelfItemBo rb = new InspectReportSelfItemBo();
						reportSelfs.add(rb);
						rb.setInspectReportId(data.getId());
						rb.setInspectReportSelfItemName(b
								.getInspectPlanSelfItemName());
						rb.setInspectReportSelfItemType(b
								.getInspectPlanSelfItemType());
						rb.setInspectReportItemContent(b
								.getInspectPlanItemContent());
					}
					inspectReportApi.saveSelfItem(reportSelfs);
				}
				List<InspectPlanResultSettingBo> results = this
						.getConclusionsByBasicId(id);
				if (results != null && results.size() > 0) {
					List<InspectReportResultsSettingBo> reportResults = new ArrayList<>();
					for (InspectPlanResultSettingBo b : results) {
						InspectReportResultsSettingBo rb = new InspectReportResultsSettingBo();
						reportResults.add(rb);
						rb.setInspectReportId(data.getId());
						rb.setInspectReportSummeriseName(b
								.getInspectPlanSummeriseName());
						rb.setInspectReportSumeriseDescrible(b
								.getInspectPlanSumeriseDescrible());
					}
				
					inspectReportApi.saveResults(reportResults);
				} else {
					List<InspectReportResultsSettingBo> reportResults = new ArrayList<>();
					InspectReportResultsSettingBo b = new InspectReportResultsSettingBo();
					b.setInspectReportId(data.getId());
					reportResults.add(b);
					inspectReportApi.saveResults(reportResults);
				}
			
				List<InspectReportContentBo> reportContents = new ArrayList<>();
				List<InspectPlanContentBo> conents = this.loadInspectionItems(id);
				if (conents != null && conents.size() > 0) {
					for (InspectPlanContentBo b : conents) {
						InspectReportContentBo rb = new InspectReportContentBo();
						reportContents.add(rb);
						
						rb.setInspectReportid(data.getId());
						rb.setInspectReportItemName(b.getInspectPlanItemName() == null ? "" : b.getInspectPlanItemName());
						rb.setInspectReportItemDescrible(b
								.getInspectPlanItemDescrible() == null ? "" : b.getInspectPlanItemDescrible());
						rb.setReportItemReferencePrefix(b
								.getInspectPlanItemReferencePrefix() == null ? "" : b.getInspectPlanItemReferencePrefix());
						rb.setReportItemReferenceSubfix(b
								.getInspectPlanItemReferenceSubfix() == null ? "" : b.getInspectPlanItemReferenceSubfix());
						rb.setInspectReportItemUnit(b.getInspectPlanItemUnit() == null ? "" : b.getInspectPlanItemUnit());
						rb.setReportItemConditionDescrible(b
								.getItemConditionDescrible() == null ? "" : b.getItemConditionDescrible());
						rb.setResourceId(b.getResourceId());
						rb.setInspectReportItemValue(b.getInspectPlanItemValue() == null ? "" : b.getInspectPlanItemValue());
						rb.setEdit(b.isEdit());
						rb.setIndicatorAsItem(b.isIndicatorAsItem());
						rb.setChildren(new ArrayList<InspectReportContentBo>());
				
					this.f(b.getId(), rb.getChildren(),b.getResourceId());
					this.diagnoseRootStatus(rb);	//遍历二级节点状态
					for (InspectReportContentBo cb : rb.getChildren()) {
						if (cb.getChildren() != null && cb.getChildren().size() > 0) {
							boolean bol = true;
							for (InspectReportContentBo childrenCB : cb.getChildren()) {
								if (!childrenCB.getInspectReportItemResult()) {
									bol = false;
								}
							}
							cb.setInspectReportItemResult(bol);
						}
					}
					}
				}
				if (reportContents != null && reportContents.size() > 0) {
					inspectReportApi.saveContents(reportContents);
				}
				
				this.updateExecTime(id, date);
			}
		}
		return false;
	}
	
	private void diagnoseRootStatus(InspectReportContentBo rb){
		for (InspectReportContentBo cb : rb.getChildren()) {
			if (cb.getChildren() != null && cb.getChildren().size() > 0) {
				for (InspectReportContentBo childrenCB : cb.getChildren()) {
					if (childrenCB.getChildren() != null && childrenCB.getChildren().size() > 0) {
						for (InspectReportContentBo c : childrenCB.getChildren()) {
							if(c.getChildren() != null && c.getChildren().size() > 0) {
								boolean bol = true;
								for(InspectReportContentBo cc : c.getChildren()) {
									if (!cc.getInspectReportItemResult()) {
										bol = false;
									}
								}
								c.setInspectReportItemResult(bol);
							}
						}
					}
				}
			}
		}
		for (InspectReportContentBo cb : rb.getChildren()) {
			if (cb.getChildren() != null && cb.getChildren().size() > 0) {
				for (InspectReportContentBo childrenCB : cb.getChildren()) {
					if (childrenCB.getChildren() != null && childrenCB.getChildren().size() > 0) {
						boolean bol = true;
						for (InspectReportContentBo c : childrenCB.getChildren()) {
							if (!c.getInspectReportItemResult()) {
								bol = false;
							}
						}
						childrenCB.setInspectReportItemResult(bol);
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private int f(long id, List<InspectReportContentBo> reportContents,Long instanceid) {
		int isRight=0;
		Boolean metricIsShow=true;
		long current=System.currentTimeMillis();
		List<InspectPlanContentBo> contents = this.loadItem(id);
		if (contents != null && contents.size() > 0) {
			for (InspectPlanContentBo b : contents) {
				InspectReportContentBo rb = new InspectReportContentBo();
				reportContents.add(rb);
				
				rb.setInspectReportItemName(b.getInspectPlanItemName() == null ? "" : b.getInspectPlanItemName());
				rb.setInspectReportItemDescrible(b
						.getInspectPlanItemDescrible() == null ? "" : b.getInspectPlanItemDescrible());
				rb.setReportItemReferencePrefix(b
						.getInspectPlanItemReferencePrefix() == null ? "" : b.getInspectPlanItemReferencePrefix());
				rb.setReportItemReferenceSubfix(b
						.getInspectPlanItemReferenceSubfix() == null ? "" : b.getInspectPlanItemReferenceSubfix());
				rb.setInspectReportItemUnit(b.getInspectPlanItemUnit() == null ? "" : b.getInspectPlanItemUnit());
				rb.setReportItemConditionDescrible(b
						.getItemConditionDescrible() == null ? "" : b.getItemConditionDescrible());
				rb.setInspectReportItemValue(b
						.getInspectPlanItemValue() == null ? "" : b.getInspectPlanItemValue());
				rb.setEdit(b.isEdit());
				rb.setIndicatorAsItem(b.isIndicatorAsItem());
				rb.setResourceId(b.getResourceId());
				rb.setType(rb.getResourceId() == null ? 1 : 2);
				rb.setInspectReportItemResult(rb.getType() == 1 ? true : false);
				
				//业务系统单独处理
				if(!"".equals(b.getIndexModelId()) && "Business".equals(b.getModelId())){
					//业务详情运行情况
					Date endDate = new Date();
					long s = endDate.getTime() - 7 * 24 * 60 * 60 * 1000;
					Date startDate = new Date(s);
					BizMainDataBo runInfoForDetail = bizMainApi.getRunInfoForDetail(rb.getResourceId(), startDate, endDate);
					//查询单个资源的容量指标信息
					List<Object> capacityMetric = bizMainApi.getCapacityMetric(rb.getResourceId());
					//历史健康度
					BizMetricHistoryDataBo healthHistoryData = bizMainApi.getHealthHistoryData(rb.getResourceId(), startDate, endDate,"halfHour");
					//响应速度
					BizMetricHistoryDataBo responseTimeHistoryData = bizMainApi.getResponseTimeHistoryData(rb.getResourceId(),startDate,endDate,"halfHour");
					//根据指标ID判断
					if(BizMetricDefine.BIZ_HEALTH_STATUS.equals(b.getIndexModelId())){//健康度
						String[] healthSholdList = healthHistoryData.getThreshold().split(",");
						int healthShold = Integer.valueOf(healthSholdList[0]);
						int curHealth = Integer.valueOf(healthHistoryData.getCurValue());
						rb.setInspectReportItemValue(healthHistoryData.getCurValue());
						if(curHealth < healthShold){
							rb.setInspectReportItemResult(false);
						}else{
							rb.setInspectReportItemResult(true);
						}
					}else if(BizMetricDefine.AVAILAVLE_RATE.equals(b.getIndexModelId())){//可用率
                        String result = runInfoForDetail.getAvailableRate().replace(b.getInspectPlanItemUnit(), "");
                        int curValue = (int) Double.parseDouble(result);
                        boolean flag = true;
                        if(curValue < 100){
                            flag = false;
                        }
                        rb.setInspectReportItemValue(result);
						if(flag){
							rb.setInspectReportItemResult(flag);
						}else{
							rb.setInspectReportItemResult(flag);
						}
					}else if(BizMetricDefine.MTTR.equals(b.getIndexModelId())){//MTTR
						String mttr = runInfoForDetail.getMTTR();
						if(StringUtil.isNull(mttr)){
							rb.setInspectReportItemValue("~");
							rb.setInspectReportItemResult(false);
						}else{
							rb.setInspectReportItemValue(mttr);
							rb.setInspectReportItemResult(true);
						}
					}else if(BizMetricDefine.MTBF.equals(b.getIndexModelId())){//MTBF
						String mtbf = runInfoForDetail.getMTBF();
						if(StringUtil.isNull(mtbf)){
							rb.setInspectReportItemValue("~");
							rb.setInspectReportItemResult(false);
						}else{
							rb.setInspectReportItemValue(mtbf);
							rb.setInspectReportItemResult(true);
						}
					}else if(BizMetricDefine.DOWN_TIME.equals(b.getIndexModelId())){//宕机时长
						String downTime = runInfoForDetail.getDownTime();
						if(StringUtil.isNull(downTime)){
							rb.setInspectReportItemValue("~");
							rb.setInspectReportItemResult(false);
						}else{
							rb.setInspectReportItemValue(downTime);
							rb.setInspectReportItemResult(true);
						}
					}else if(BizMetricDefine.OUTAGE_TIMES.equals(b.getIndexModelId())){//宕机次数
						String outageTimes = runInfoForDetail.getOutageTimes();
						if(StringUtil.isNull(outageTimes)){
							rb.setInspectReportItemValue("~");
							rb.setInspectReportItemResult(false);
						}else{
							rb.setInspectReportItemValue(outageTimes);
							rb.setInspectReportItemResult(true);
						}
					/*}else if(BizMetricDefine.RESPONSE_TIME.equals(b.getIndexModelId())){//取消响应速度
						String threshold = responseTimeHistoryData.getThreshold();
						rb.setInspectReportItemResult(true);
						if(StringUtil.isNull(threshold)){
							rb.setInspectReportItemValue("~");
						}else{
							String[] requestSholdList = threshold.split(",");
							long requestShold = Long.valueOf(requestSholdList[0]);
                            double curStr = Double.parseDouble(responseTimeHistoryData.getCurValue().trim());
							long curValue = (long) curStr;
							if(StringUtil.isNull(responseTimeHistoryData.getCurValue())){
								rb.setInspectReportItemValue("~");
							}else{
								if(curValue > requestShold){
									rb.setInspectReportItemResult(false);
								}
							}
						}*/
					}else if(BizMetricDefine.HOST_CAPACITY.equals(b.getIndexModelId())){//计算容量
						for(Object obj : capacityMetric){
							if(obj instanceof BizCalculateCapacityMetric){
								BizCalculateCapacityMetric calculate = (BizCalculateCapacityMetric)obj;
								double cpuRate = Double.valueOf(calculate.getCpuRate());
								rb.setInspectReportItemValue(calculate.getCpuRate());
								rb.setInspectReportItemResult(true);
								if(cpuRate >= 70d){
									rb.setInspectReportItemResult(false);
								}
								break;
							}
						}
					}else if(BizMetricDefine.STORAGE_CAPACITY.equals(b.getIndexModelId())){//存储容量
						for(Object obj : capacityMetric){
							if(obj instanceof BizStoreCapacityMetric){
								BizStoreCapacityMetric store = (BizStoreCapacityMetric)obj;
								double useRate = Double.valueOf(store.getUseRate());
								rb.setInspectReportItemValue(store.getUseRate());
								rb.setInspectReportItemResult(true);
								if(useRate >= 70d){
									rb.setInspectReportItemResult(false);
								}
								break;
							}
						}
					}else if(BizMetricDefine.DATABASE_CAPACITY.equals(b.getIndexModelId())){//数据库容量
						for(Object obj : capacityMetric){
							if(obj instanceof BizDatabaseCapacityMetric){
								BizDatabaseCapacityMetric dataBase = (BizDatabaseCapacityMetric)obj;
								double useRate = Double.valueOf(dataBase.getUseRate());
								rb.setInspectReportItemValue(dataBase.getUseRate());
								rb.setInspectReportItemResult(true);
								if(useRate >= 70d){
									rb.setInspectReportItemResult(false);
								}
								break;
							}
						}
					}else if(BizMetricDefine.BANDWIDTH_CAPACITY.equals(b.getIndexModelId())){//带宽容量
						for(Object obj : capacityMetric){
							if(obj instanceof BizBandwidthCapacityMetric){
								BizBandwidthCapacityMetric widthCapacity = (BizBandwidthCapacityMetric)obj;
								double useRate = Double.valueOf(widthCapacity.getUseRate());
								rb.setInspectReportItemValue(widthCapacity.getUseRate());
								rb.setInspectReportItemResult(true);
								if(useRate >= 70d){
									rb.setInspectReportItemResult(false);
								}
								break;
							}
						}
					}
				}else{
					if (b.getResourceId() != null && b.getIndexModelId() != null
							&& !"".equals(b.getIndexModelId())) {
						try {
							ResourceInstance res = this.resourceInstanceService
									.getResourceInstance(b.getResourceId());
							if (res == null
									|| res.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
								rb.setInspectReportItemValue("");
								rb.setInspectReportItemResult(false);
								continue;
							}
							ResourceMetricDef rmf = capacityService
									.getResourceMetricDef(
											String.valueOf(b.getModelId()),
											b.getIndexModelId());
							MetricData md = null;
							metricIsShow=true;
							if(rmf == null){
								rmf = new ResourceMetricDef();
								CustomMetric customMetric=null;
							boolean bl=	isNumeric(b.getIndexModelId());
							if(bl==true){//查自定义指标
								customMetric = customMetricService.getCustomMetric(b.getIndexModelId());
							}
								if(customMetric != null){
									//自定义
									rmf.setMetricType(customMetric.getCustomMetricInfo().getStyle());
									md = metricDataService.getCustomerMetricData(b.getResourceId(), b.getIndexModelId());
									ProfileMetric metric=	profileService.getMetricByInstanceIdAndMetricId(instanceid, b.getIndexModelId());
									if(metric!=null){
										metricIsShow=this.isShowMerticValue(metric,null,md.getCollectTime().getTime());
	//									currentCollect=md.getCollectTime().getTime();
									}else{
										CustomMetric mt=null;
										try {
											mt = customMetricService.getCustomMetric(b.getIndexModelId());
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
	//									CustomMetric mt= new CustomMetric();
										if(mt!=null){
											//自定义
											CustomMetricInfo info=mt.getCustomMetricInfo();
											if(info!=null){
												metricIsShow=this.isShowMerticValue(null,info,md.getCollectTime().getTime());
											}
										}
									}
									if(metricIsShow==false){
										rb.setInspectReportItemValue("--");
									}
						
								
								}else{
									logger.error("Get metric is null , metricId : " + b.getIndexModelId() + " , resourceId : " + b.getModelId());
									continue;
								}
							}
							
							if (MetricTypeEnum.AvailabilityMetric == rmf
									.getMetricType()) {// 可用性指标
								long jobTime=0L;
								long currentCollect=0L;
								ProfileMetric metric=	profileService.getMetricByInstanceIdAndMetricId(instanceid, b.getIndexModelId());
								if(md == null){
									md = metricDataService.getMetricAvailableData(
											b.getResourceId(), b.getIndexModelId());
								}
									//可用性指标屏蔽掉显示判断 苏州气象局二开需求
//									if(metric!=null && md!=null){
//										metricIsShow=this.isShowMerticValue(metric,null,md.getCollectTime().getTime());
	//								currentCollect=md.getCollectTime().getTime();
//									}
								if (md != null) {
									String[] value = md.getData();
									String itmevalue = "";
									if (value != null && value.length > 0) {
										for (String rs : value) {
											if ("".equalsIgnoreCase(rs)
													|| "null".equalsIgnoreCase(rs)
													|| rs == null) {
												continue;
											} else {
												if (itmevalue == "") {
													itmevalue = rs;
												} else {
													itmevalue = itmevalue + " "
															+ rs;
												}
											}
										}
									}
									logger.info("可用性指标");
									logger.info("metricIsShow: "+metricIsShow);
									if(metricIsShow==false){
										rb.setInspectReportItemValue("--");
									}else{
										rb.setInspectReportItemValue("1"
												.equals(itmevalue) ? "正常" : "0"
												.equals(itmevalue) ? "异常" : "未知");
									}
									
									rb.setInspectReportItemResult("1"
											.equals(itmevalue) ? true : false);
								}
							} else if (MetricTypeEnum.InformationMetric == rmf
									.getMetricType()) {// 信息指标
								Long jobTime=0L;
								Long currentCollect=0L;
	//							ProfileMetric metric2=profileService.getMetricByProfileIdAndMetricId(1501, b.getIndexModelId());
								ProfileMetric metric=	profileService.getMetricByInstanceIdAndMetricId(instanceid, b.getIndexModelId());
								if(md == null){
								/*	CustomMetric customMetric=null;
									try {
										customMetric = customMetricService.getCustomMetric(b.getIndexModelId());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
	//								CustomMetric customMetric= new CustomMetric();
									if(metric==null){//指标自定义
										md = metricDataService.getCustomerMetricData(b.getResourceId(), b.getIndexModelId());
									}else{
									md=infoMetricQueryAdaptService.getMetricInfoData(b.getResourceId(), b.getIndexModelId());
	//									md = metricDataService.getMetricInfoData(
	//										b.getResourceId(), b.getIndexModelId());
									}
	//								System.out.println(md.getCollectTime().getTime());
									//自定义指标
								/*	List<CustomMetric> list= customMetricService.getCustomMetricsByInstanceId(b.getResourceId());
									if(list!=null){
										for (CustomMetric cm : list) {
										if(cm.getCustomMetricInfo().getId().equals(b.getIndexModelId())){
											if(cm.getCustomMetricInfo().isMonitor()==false){//未监控
	//											System.out.println("xinxi");
	//											System.out.println("当前时间"+current);
	//											System.out.println("采集时间"+md.getCollectTime().getTime());
												long collectDatetemp=current-md.getCollectTime().getTime();
												long collectDate=collectDatetemp/1000/60/60;
	//											System.out.println("距离时间"+collectDate);
												if(collectDate>=2){
													metricIsMonitor=false;	
												}
											
											}
										}
									}
								}*/
								}
								//过滤虚拟化
                                String[] vmList = {"VM","VMware","Xen","FusionCompute","FusionComputeOnePointThree","kvm"};
                                String parentCategoryId = res.getParentCategoryId();
                                if(metric!=null && md!=null){//获取指标采集周期，并判断是否显示巡检出的指标值
                                    boolean flag = true;
                                    for(String v : vmList){
                                        if(v.equals(parentCategoryId)){
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if(flag){
                                        metricIsShow=isShowMerticValue(metric,null,md.getCollectTime().getTime());
                                    }
                                    //								currentCollect=md.getCollectTime().getTime();
								}
								if (md != null) {
									String[] value = md.getData();
									String itmevalue = "";
									if (value != null && value.length > 0) {
										for (String rs : value) {
											if ("".equalsIgnoreCase(rs)
													|| "null".equalsIgnoreCase(rs)
													|| rs == null) {
												continue;
											} else {
												if (itmevalue == "") {
													itmevalue = rs;
												} else {
													itmevalue = itmevalue + " "
															+ rs;
												}
											}
										}  
										logger.info("信息指标--");
										logger.info("metricIsShow: "+metricIsShow);
										if(metricIsShow==false){
	//										if(metricIsMonitor==false){
											rb.setInspectReportItemValue("--");
										}else{
										rb.setInspectReportItemValue(itmevalue);
										}
										rb.setInspectReportItemResult(StringUtil
												.isNull(itmevalue) ? false : true);
										if (!StringUtil.isNull(itmevalue)
												&& "连续运行时间"
														.equals(rb
																.getInspectReportItemName())) {
											rb.setInspectReportItemUnit("");
											long time = (long) Double
													.parseDouble(itmevalue);
											long day = time / (24 * 60 * 60);
											long hour = time % (24 * 60 * 60)
													/ (60 * 60);
											long min = time % (24 * 60 * 60)
													% (60 * 60) / 60;
											long second = time % (24 * 60 * 60)
													% (60 * 60) % 60;
											String valueStr = "";
											if (day > 0) {
												valueStr = day + "天" + hour + "时"
														+ min + "分" + second + "秒";
											} else if (hour > 0) {
												valueStr = hour + "时" + min + "分"
														+ second + "秒";
											} else if (min > 0) {
												valueStr = min + "分" + second + "秒";
											} else {
												valueStr = second + "秒";
											}
											logger.info("信息指标");
											logger.info("metricIsShow: "+metricIsShow);
											if(metricIsShow==false){
	//											if(metricIsMonitor==false){
												rb.setInspectReportItemValue("--");
											}else{
											rb.setInspectReportItemValue(valueStr);
											}
										}
									}
								}
							} else if (MetricTypeEnum.PerformanceMetric == rmf
									.getMetricType()) {// 性能
								Long jobTime=0L;
								Long currentCollect=0L;
								ProfileMetric metric=	profileService.getMetricByInstanceIdAndMetricId(instanceid, b.getIndexModelId());
								if(md == null){
									
								/*	CustomMetric customMetric=null;
									try {
										customMetric = customMetricService.getCustomMetric(b.getIndexModelId());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}*/
	//								CustomMetric customMetric= new CustomMetric();
									if(metric==null){//指标自定义
										md = metricDataService.getCustomerMetricData(b.getResourceId(), b.getIndexModelId());
									}else{
										md = metricDataService.getMetricPerformanceData(
												b.getResourceId(), b.getIndexModelId());
									}
								}
								if(metric!=null && md!=null){
									metricIsShow=this.isShowMerticValue(metric,null,md.getCollectTime().getTime());
	//								currentCollect=md.getCollectTime().getTime();
									
								}
								if (md != null) {
									StringBuilder value = new StringBuilder();
									String[] itme = md.getData();
									String itmevalue = "";
									if (itme != null && itme.length > 0) {
										for (String rs : itme) {
											if ("".equalsIgnoreCase(rs)
													|| "null".equalsIgnoreCase(rs)
													|| rs == null) {
												continue;
											} else {
												if (itmevalue == "") {
													itmevalue = rs;
												} else {
													itmevalue = itmevalue + " "
															+ rs;
												}
												value.append(rs);
											}
										}
										logger.info("性能指标");
										logger.info("metricIsShow: "+metricIsShow);
										if(metricIsShow==false){
	//										if(metricIsMonitor==false){
											rb.setInspectReportItemValue("--");
										}else{
											rb.setInspectReportItemValue(itmevalue);
										}
										if (value.length() > 0
												&& (!StringUtil
														.isNull(rb
																.getReportItemReferencePrefix()) || !StringUtil
														.isNull(rb
																.getReportItemReferenceSubfix()))) {
											if (StringUtil
													.isNull(rb
															.getReportItemReferenceSubfix())
													&& StringUtil
															.isNumber(rb
																	.getReportItemReferencePrefix())
													&& StringUtil.isNumber(value
															.toString())) {
												Double prefix = Double
														.parseDouble(rb
																.getReportItemReferencePrefix());
												Double v = Double.parseDouble(value
														.toString());
												rb.setInspectReportItemResult(prefix <= v);
											} else if (StringUtil
													.isNull(rb
															.getReportItemReferenceSubfix())
													&& !StringUtil
															.isNumber(rb
																	.getReportItemReferencePrefix())) {
												rb.setInspectReportItemResult(value.toString().equals(rb.getReportItemReferencePrefix()));
											} else if (StringUtil
													.isNull(rb
															.getReportItemReferencePrefix())
													&& StringUtil
															.isNumber(rb
																	.getReportItemReferenceSubfix())
													&& StringUtil.isNumber(value
															.toString())) {
												Double subfix = Double
														.parseDouble(rb
																.getReportItemReferenceSubfix());
												Double v = Double.parseDouble(value
														.toString());
												rb.setInspectReportItemResult(subfix >= v);
											} else if (StringUtil
													.isNull(rb
															.getReportItemReferencePrefix())
													&& !StringUtil
															.isNumber(rb
																	.getReportItemReferenceSubfix())) {
												rb.setInspectReportItemResult(value.toString().equals(rb.getReportItemReferenceSubfix()));
											} else if (StringUtil
													.isNumber(rb
															.getReportItemReferencePrefix())
													&& StringUtil
															.isNumber(rb
																	.getReportItemReferenceSubfix())
													&& StringUtil.isNumber(value
															.toString())) {
												Double prefix = Double
														.parseDouble(rb
																.getReportItemReferencePrefix());
												Double subfix = Double
														.parseDouble(rb
																.getReportItemReferenceSubfix());
												Double v = Double.parseDouble(value
														.toString());
												rb.setInspectReportItemResult(prefix <= v
														&& v <= subfix);
											} else {
												rb.setInspectReportItemResult(value.toString().equals(rb.getReportItemReferencePrefix())
														|| value.toString().equals(rb.getReportItemReferenceSubfix()));
											}
										}
									}
								}
							}
						} catch (Exception e) {
							isRight=1;
							logger.warn(e.getMessage());
							
						}
					}
				}
				rb.setChildren(new ArrayList<InspectReportContentBo>());
				this.f(b.getId(), rb.getChildren(),b.getResourceId());
			}
		}
		return isRight;
	}
	public final static boolean isNumeric(String s) {  
        if (s != null && !"".equals(s.trim()))  
            return s.matches("^[0-9]*$");  
        else  
            return false;  
    } 
	/**
	 * 根据资源ID查询指标列表
	 */
	@Override
	public List<ReportResourceMetric> getMetricListByResource(
			String[] resourceIdList) {

		Map<String, List<ReportResourceMetric>> metricMap = new HashMap<String, List<ReportResourceMetric>>();
		for (String resourceObj : resourceIdList) {
			String resourceId = "";
			String instanceId = "";
			if(!Util.isEmpty(resourceObj)){
				logger.error("resourceObj: "+resourceObj);
				String[] resource = resourceObj.split(";");
				resourceId = resource[0];
				instanceId = resource[1];
			}
			ResourceDef def = capacityService.getResourceDefById(resourceId);
			if (def == null) {
				logger.error("CapacityService.getResourceDefById is null,resourceId : "
						+ resourceId);
				continue;
			}
			List<ReportResourceMetric> metricList = new ArrayList<ReportResourceMetric>();
			for (ResourceMetricDef metricDef : def.getMetricDefs()) {
				if (!metricDef.isDisplay()) {
					continue;
				}
				ReportResourceMetric metric = new ReportResourceMetric();
				BeanUtils.copyProperties(metricDef, metric);
				metric.setStyle(metricDef.getMetricType());
				metricList.add(metric);
			}
			//自定义指标
			try {
				logger.error("customid: "+instanceId);
				List<CustomMetric> customMetrics = customMetricService.getCustomMetricsByInstanceId(Long.parseLong(instanceId));
				if(customMetrics != null && customMetrics.size() > 0){
					for(CustomMetric cm:customMetrics){
						ReportResourceMetric metric = new ReportResourceMetric();
						BeanUtils.copyProperties(cm.getCustomMetricInfo(), metric);
						metricList.add(metric);
					}
				}
			} catch (CustomMetricException e) {
				logger.error("CustomMetricException",e);
			}
			metricMap.put(resourceId, metricList);
		}
		// 取所有集合的交集
		List<ReportResourceMetric> metricList = new ArrayList<ReportResourceMetric>();
		for (String resourceId : metricMap.keySet()) {
			if (metricList.size() <= 0) {
				metricList = metricMap.get(resourceId);
				continue;
			}
			List<ReportResourceMetric> metricLists = metricMap.get(resourceId);
			metricList.retainAll(metricLists);
		}

		// 根据BUG #2089
		// 排列顺序请按照指标名称升序排列，信息指标优先，其次是可用性指标，最后为性能指标在指标待选列表中和巡检项列表中均按照此顺序显示
		List<ReportResourceMetric> performanceMmetricList = new ArrayList<ReportResourceMetric>();
		List<ReportResourceMetric> avaliabeeMmetricList = new ArrayList<ReportResourceMetric>();
		List<ReportResourceMetric> informationMmetricList = new ArrayList<ReportResourceMetric>();
		for (ReportResourceMetric def : metricList) {
			if ("PerformanceMetric".equals(def.getStyle().name())) {
				performanceMmetricList.add(def);
			} else if ("InformationMetric".equals(def.getStyle().name())) {
				informationMmetricList.add(def);
			} else if ("AvailabilityMetric".equals(def.getStyle().name())) {
				avaliabeeMmetricList.add(def);
			}
		}
		Collections.sort(informationMmetricList);
		Collections.sort(avaliabeeMmetricList);
		Collections.sort(performanceMmetricList);
		metricList.clear();
		metricList.addAll(informationMmetricList);
		metricList.addAll(avaliabeeMmetricList);
		metricList.addAll(performanceMmetricList);
		return metricList;

	}

	@Override
	public int delPlan(long[] ids) throws ClassNotFoundException,
			SchedulerException {
		this.inspectPlanDao.del(ids);
		this.inspectPlanDao.delSelf(ids);
		this.inspectPlanDao.delResult(ids);
		this.inspectPlanDao.delAll(ids);
		Long[] itemIds = this.inspectPlanDao.getThreeItems(ids);
		if (itemIds != null && itemIds.length > 0) {
			this.inspectPlanDao.delInspectionItem(itemIds);
//			this.inspectPlanDao.delInspectionItemParent(itemIds);
		}
		try {
			for (Long id : ids) {
				this.scheduleManager.deleteJob(JOB_PLAN + id);
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return 1;
	}

	@Override
	public int delItems(long[] itemIds) {
		Long[] itemIdsArray = this.inspectPlanDao
				.getThreeItemsByItemIds(itemIds);
		if (itemIdsArray != null && itemIdsArray.length > 0) {
			this.inspectPlanDao.delInspectionItem(itemIdsArray);
		}
		return 1;
	}
	//避免oracle字段为clob类型时,批量插入null值导致的sql类型错误
	private static void checkCLOBColumn(InspectPlanContentBo ipcb){
		if(null==ipcb.getInspectPlanItemReferenceSubfix()){
			ipcb.setInspectPlanItemReferenceSubfix("");
		}
		if(null==ipcb.getInspectPlanItemReferencePrefix()){
			ipcb.setInspectPlanItemReferencePrefix("");
		}
		if(null==ipcb.getInspectPlanItemValue()){
			ipcb.setInspectPlanItemValue("");
		}
		if(null==ipcb.getItemConditionDescrible()){
			ipcb.setItemConditionDescrible("");
		}
	}
	
	@Override
	public long saveCopy(long id, long userId) {
		long newId = this.sequence.next();
	this.inspectPlanDao.copyPlan(id, newId, userId);
	List<InspectPlanSelfItemBo> selfs = this.inspectPlanDao.getSelfItem(id);
	if (selfs != null && selfs.size() > 0) {
		for (InspectPlanSelfItemBo s : selfs) {
			s.setId(this.sequence.next());
			s.setInspectPlanId(newId);
		}
		this.inspectPlanDao.addSelfItems(selfs);
	}
	List<InspectPlanResultSettingBo> results = this.inspectPlanDao
			.getConclusionsByBasicId(id);
	if (results != null && results.size() > 0) {
		for (InspectPlanResultSettingBo r : results) {
			r.setId(this.sequence.next());
			r.setInspectPlanId(newId);
		}
		this.inspectPlanDao.saveConclusions(results);
	}
	List<InspectPlanContentBo> contensData = new ArrayList<InspectPlanContentBo>();
	List<InspectPlanContentBo> contens = this.inspectPlanDao
			.loadInspectionItems(id);
	if (contens != null && contens.size() > 0) {
		for (InspectPlanContentBo c : contens) {
			checkCLOBColumn(c);
			contensData.add(c);
			
			List<InspectPlanContentBo> contens2 = this.inspectPlanDao
					.loadItem(c.getId());
			c.setId(this.sequence.next());
			c.setInspectPlanId(newId);
			if (contens2 != null && contens2.size() > 0) {
				for (InspectPlanContentBo c2 : contens2) {
					checkCLOBColumn(c2);
					contensData.add(c2);
					
					List<InspectPlanContentBo> contens3 = this.inspectPlanDao
							.loadItem(c2.getId());
					c2.setId(this.sequence.next());
					c2.setInspectPlanParentId(c.getId());
					if (contens3 != null && contens3.size() > 0) {
						for (InspectPlanContentBo c3 : contens3) {
							checkCLOBColumn(c3);
							contensData.add(c3);
							List<InspectPlanContentBo> contens4 = this.inspectPlanDao
									.loadItem(c3.getId());
							c3.setId(this.sequence.next());
							c3.setInspectPlanParentId(c2.getId());
							if(contens4 != null && contens4.size() > 0){
								for (InspectPlanContentBo c4 : contens4){
									checkCLOBColumn(c4);
									contensData.add(c4);
									c4.setId(this.sequence.next());
									c4.setInspectPlanParentId(c3.getId());
								}
							}
						}
//						contensData.addAll(0, contens3);
					}
				}
//				contensData.addAll(0, contens2);
			}
		}
//		contensData.addAll(0, contens);
		this.inspectPlanDao.addItems(contensData);
	}
	
	return newId;
	}

	@Override
	public int updateReportName(long planId, String name) {
		int u = this.inspectPlanDao.updateReportName(planId, name);
		this.inspectPlanDao.updateEditDate(planId);
		return u;
	}

	@Override
	public int updateItems(long dirId, String json) {
		Long[] itemIds = this.inspectPlanDao.getTwoItems(new long[] { dirId });
		if (itemIds != null && itemIds.length > 0) {
			this.inspectPlanDao.delInspectionItem(itemIds);
		}
		if (!StringUtil.isNull(json)) {
			JSONArray jsonArray = JSONArray.parseArray(json);
			if (jsonArray.size() > 0) {
				List<InspectPlanContentBo> contensData = new ArrayList<InspectPlanContentBo>();
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					InspectPlanContentBo c = new InspectPlanContentBo();
					contensData.add(c);
					c.setId(this.sequence.next());
					c.setInspectPlanParentId(dirId);
					if (obj.getString("inspectPlanItemName") != null) {
						c.setInspectPlanItemName(obj
								.getString("inspectPlanItemName"));
					}else {
						c.setInspectPlanItemName("");
					}
					if (obj.getString("inspectPlanItemDescrible") != null) {
						c.setInspectPlanItemDescrible(obj
								.getString("inspectPlanItemDescrible"));
					}else {
						c.setInspectPlanItemDescrible("");
					}
					if (obj.getString("inspectPlanItemReferencePrefix") != null) {
						c.setInspectPlanItemReferencePrefix(obj
								.getString("inspectPlanItemReferencePrefix"));
					}else {
						c.setInspectPlanItemReferencePrefix("");
					}
					if (obj.getString("inspectPlanItemReferenceSubfix") != null) {
						c.setInspectPlanItemReferenceSubfix(obj
								.getString("inspectPlanItemReferenceSubfix"));
					}else {
						c.setInspectPlanItemReferenceSubfix("");
					}
					if (obj.getString("inspectPlanItemUnit") != null) {
						c.setInspectPlanItemUnit(obj
								.getString("inspectPlanItemUnit"));
					}else {
						c.setInspectPlanItemUnit("");
					}
					if (obj.getString("itemConditionDescrible") != null) {
						c.setItemConditionDescrible(obj
								.getString("itemConditionDescrible"));
					}else {
						c.setItemConditionDescrible("");
					}
					if (obj.getString("modelId") != null) {
						c.setModelId(obj.getString("modelId"));
					}else {
						c.setModelId("");
					}
					if (obj.getLong("resourceId") != null) {
						c.setResourceId(obj.getLong("resourceId"));
					}
					if (obj.getString("indexModelId") != null) {
						c.setIndexModelId(obj.getString("indexModelId"));
					}else {
						c.setIndexModelId("");
					}
					if (obj.getString("inspectPlanItemValue") != null) {
						c.setInspectPlanItemValue(obj
								.getString("inspectPlanItemValue"));
					}else {
						c.setInspectPlanItemValue("");
					}
					if (obj.getInteger("type") != null) {
						c.setType(obj.getInteger("type"));
					}
					if (obj.getBoolean("indicatorAsItem") != null) {
						c.setIndicatorAsItem(obj.getBoolean("indicatorAsItem"));
					}
					c.setSort(i);
					if (obj.containsKey("children")) {
						JSONArray array = obj.getJSONArray("children");
						if (array != null && array.size() > 0) {
							for (int j = 0; j < array.size(); j++) {
								obj = array.getJSONObject(j);
//								if(obj.getString("modelId") != null && "Business".equals(obj.getString("modelId")) &&
//										obj.getString("parentId") != null){
								if(obj.containsKey("children") && obj.getJSONArray("children") != null &&
										obj.getJSONArray("children").size() > 0){
									InspectPlanContentBo cc = new InspectPlanContentBo();
									contensData.add(cc);
									cc.setId(this.sequence.next());
									cc.setInspectPlanParentId(c.getId());
									if (obj.getString("inspectPlanItemName") != null) {
										cc.setInspectPlanItemName(obj
												.getString("inspectPlanItemName"));
									}else {
										cc.setInspectPlanItemName("");
									}
									if (obj.getString("inspectPlanItemDescrible") != null) {
										cc.setInspectPlanItemDescrible(obj
												.getString("inspectPlanItemDescrible"));
									}else {
										cc.setInspectPlanItemDescrible("");
									}
									if (obj.getString("inspectPlanItemReferencePrefix") != null) {
										cc.setInspectPlanItemReferencePrefix(obj
												.getString("inspectPlanItemReferencePrefix"));
									}else {
										cc.setInspectPlanItemReferencePrefix("");
									}
									if (obj.getString("inspectPlanItemReferenceSubfix") != null) {
										cc.setInspectPlanItemReferenceSubfix(obj
												.getString("inspectPlanItemReferenceSubfix"));
									}else {
										cc.setInspectPlanItemReferenceSubfix("");
									}
									if (obj.getString("inspectPlanItemUnit") != null) {
										cc.setInspectPlanItemUnit(obj
												.getString("inspectPlanItemUnit"));
									}else {
										c.setInspectPlanItemUnit("");
									}
									if (obj.getString("itemConditionDescrible") != null) {
										cc.setItemConditionDescrible(obj
												.getString("itemConditionDescrible"));
									}else {
										cc.setItemConditionDescrible("");
									}
									if (obj.getString("modelId") != null) {
										cc.setModelId(obj.getString("modelId"));
									}else {
										cc.setModelId("");
									}
									if (obj.getLong("resourceId") != null) {
										cc.setResourceId(obj.getLong("resourceId"));
									}
									if (obj.getString("indexModelId") != null) {
										c.setIndexModelId(obj.getString("indexModelId"));
									}else {
										c.setIndexModelId("");
									}
									if (obj.getString("inspectPlanItemValue") != null) {
										cc.setInspectPlanItemValue(obj
												.getString("inspectPlanItemValue"));
									}else {
										cc.setInspectPlanItemValue("");
									}
									if (obj.getInteger("type") != null) {
										cc.setType(obj.getInteger("type"));
									}
									if (obj.getBoolean("indicatorAsItem") != null) {
										cc.setIndicatorAsItem(obj.getBoolean("indicatorAsItem"));
									}
									cc.setSort(j);
									if (obj.containsKey("children")) {
										JSONArray ccArray = obj.getJSONArray("children");
										if (ccArray != null && ccArray.size() > 0) {
											for (int x = 0; x < ccArray.size(); x++) {
												JSONObject jsonObject = ccArray.getJSONObject(x);
												InspectPlanContentBo c2 = new InspectPlanContentBo();
												contensData.add(c2);
												c2.setId(this.sequence.next());
												c2.setInspectPlanParentId(cc.getId());
												if (jsonObject.getString("inspectPlanItemName") != null) {
													c2.setInspectPlanItemName(jsonObject
															.getString("inspectPlanItemName"));
												}else {
													c2.setInspectPlanItemName("");
												}
												if (jsonObject.getString("inspectPlanItemDescrible") != null) {
													c2.setInspectPlanItemDescrible(jsonObject
															.getString("inspectPlanItemDescrible"));
												}else {
													c2.setInspectPlanItemDescrible("");
												}
												if (jsonObject.getString("inspectPlanItemReferencePrefix") != null) {
													c2.setInspectPlanItemReferencePrefix(jsonObject
															.getString("inspectPlanItemReferencePrefix"));
												}else {
													c2.setInspectPlanItemReferencePrefix("");
												}
												if (jsonObject.getString("inspectPlanItemReferenceSubfix") != null) {
													c2.setInspectPlanItemReferenceSubfix(jsonObject
															.getString("inspectPlanItemReferenceSubfix"));
												}else {
													c2.setInspectPlanItemReferenceSubfix("");
												}
												if (jsonObject.getString("inspectPlanItemUnit") != null) {
													c2.setInspectPlanItemUnit(jsonObject
															.getString("inspectPlanItemUnit"));
												}else {
													c2.setInspectPlanItemUnit("");
												}
												if (jsonObject.getString("inspectPlanItemValue") != null) {
													c2.setInspectPlanItemValue(jsonObject
															.getString("inspectPlanItemValue"));
												}else {
													c2.setInspectPlanItemValue("");
												}
												if (jsonObject.getString("itemConditionDescrible") != null) {
													c2.setItemConditionDescrible(jsonObject
															.getString("itemConditionDescrible"));
												}else {
													c2.setItemConditionDescrible("");
												}
												if (jsonObject.getString("modelId") != null) {
													c2.setModelId(jsonObject.getString("modelId"));
												}else {
													c2.setModelId("");
												}
												if (jsonObject.getLong("resourceId") != null) {
													c2.setResourceId(jsonObject.getLong("resourceId"));
												}
												if (jsonObject.getString("indexModelId") != null) {
													c2.setIndexModelId(jsonObject.getString("indexModelId"));
												}else {
													c2.setIndexModelId("");
												}
												if (jsonObject.getInteger("type") != null) {
													c2.setType(jsonObject.getInteger("type"));
												}
												if (jsonObject.getBoolean("indicatorAsItem") != null) {
													c2.setIndicatorAsItem(jsonObject.getBoolean("indicatorAsItem"));
												}
												c2.setSort(x);
												if (jsonObject.containsKey("children")) {
													JSONArray cccArray = jsonObject.getJSONArray("children");
													if (cccArray != null && cccArray.size() > 0) {
														for (int z = 0; z < cccArray.size(); z++) {
															JSONObject cjsonObject = cccArray.getJSONObject(z);
															InspectPlanContentBo c3 = new InspectPlanContentBo();
															contensData.add(c3);
															c3.setId(this.sequence.next());
															c3.setInspectPlanParentId(c2.getId());
															if (cjsonObject.getString("inspectPlanItemName") != null) {
																c3.setInspectPlanItemName(cjsonObject
																		.getString("inspectPlanItemName"));
															}else {
																c3.setInspectPlanItemName("");
															}
															if (cjsonObject.getString("inspectPlanItemDescrible") != null) {
																c3.setInspectPlanItemDescrible(cjsonObject
																		.getString("inspectPlanItemDescrible"));
															}else {
																c3.setInspectPlanItemDescrible("");
															}
															if (cjsonObject.getString("inspectPlanItemReferencePrefix") != null) {
																c3.setInspectPlanItemReferencePrefix(cjsonObject
																		.getString("inspectPlanItemReferencePrefix"));
															}else {
																c3.setInspectPlanItemReferencePrefix("");
															}
															if (cjsonObject.getString("inspectPlanItemReferenceSubfix") != null) {
																c3.setInspectPlanItemReferenceSubfix(cjsonObject
																		.getString("inspectPlanItemReferenceSubfix"));
															}else {
																c3.setInspectPlanItemReferenceSubfix("");
															}
															if (cjsonObject.getString("inspectPlanItemUnit") != null) {
																c3.setInspectPlanItemUnit(cjsonObject
																		.getString("inspectPlanItemUnit"));
															}else {
																c3.setInspectPlanItemUnit("");
															}
															if (cjsonObject.getString("inspectPlanItemValue") != null) {
																c3.setInspectPlanItemValue(cjsonObject
																		.getString("inspectPlanItemValue"));
															}else {
																c3.setInspectPlanItemValue("");
															}
															if (cjsonObject.getString("itemConditionDescrible") != null) {
																c3.setItemConditionDescrible(cjsonObject
																		.getString("itemConditionDescrible"));
															}else {
																c3.setItemConditionDescrible("");
															}
															if (cjsonObject.getString("modelId") != null) {
																c3.setModelId(cjsonObject.getString("modelId"));
															}else {
																c3.setModelId("");
															}
															if (cjsonObject.getLong("resourceId") != null) {
																c3.setResourceId(cjsonObject.getLong("resourceId"));
															}
															if (cjsonObject.getString("indexModelId") != null) {
																c3.setIndexModelId(cjsonObject.getString("indexModelId"));
															}else {
																c3.setIndexModelId("");
															}
															if (cjsonObject.getInteger("type") != null) {
																c3.setType(cjsonObject.getInteger("type"));
															}
															if (cjsonObject.getBoolean("indicatorAsItem") != null) {
																c3.setIndicatorAsItem(cjsonObject.getBoolean("indicatorAsItem"));
															}
															c3.setSort(z);
														}
													}
												}
											}
										}
									}
								}else{
									InspectPlanContentBo c1 = new InspectPlanContentBo();
									contensData.add(c1);
									c1.setId(this.sequence.next());
									c1.setInspectPlanParentId(c.getId());
									if (obj.getString("inspectPlanItemName") != null) {
										c1.setInspectPlanItemName(obj
												.getString("inspectPlanItemName"));
									}else {
										c1.setInspectPlanItemName("");
									}
									if (obj.getString("inspectPlanItemDescrible") != null) {
										c1.setInspectPlanItemDescrible(obj
												.getString("inspectPlanItemDescrible"));
									}else {
										c1.setInspectPlanItemDescrible("");
									}
									if (obj.getString("inspectPlanItemReferencePrefix") != null) {
										c1.setInspectPlanItemReferencePrefix(obj
												.getString("inspectPlanItemReferencePrefix"));
									}else {
										c1.setInspectPlanItemReferencePrefix("");
									}
									if (obj.getString("inspectPlanItemReferenceSubfix") != null) {
										c1.setInspectPlanItemReferenceSubfix(obj
												.getString("inspectPlanItemReferenceSubfix"));
									}else {
										c1.setInspectPlanItemReferenceSubfix("");
									}
									if (obj.getString("inspectPlanItemUnit") != null) {
										c1.setInspectPlanItemUnit(obj
												.getString("inspectPlanItemUnit"));
									}else {
										c1.setInspectPlanItemUnit("");
									}
									if (obj.getString("inspectPlanItemValue") != null) {
										c1.setInspectPlanItemValue(obj
												.getString("inspectPlanItemValue"));
									}else {
										c1.setInspectPlanItemValue("");
									}
									if (obj.getString("itemConditionDescrible") != null) {
										c1.setItemConditionDescrible(obj
												.getString("itemConditionDescrible"));
									}else {
										c1.setItemConditionDescrible("");
									}
									if (obj.getString("modelId") != null) {
										c1.setModelId(obj.getString("modelId"));
									}else {
										c1.setModelId("");
									}
									if (obj.getLong("resourceId") != null) {
										c1.setResourceId(obj.getLong("resourceId"));
									}
									if (obj.getString("indexModelId") != null) {
										c1.setIndexModelId(obj.getString("indexModelId"));
									}else {
										c1.setIndexModelId("");
									}
									if (obj.getInteger("type") != null) {
										c1.setType(obj.getInteger("type"));
									}
									if (obj.getBoolean("indicatorAsItem") != null) {
										c1.setIndicatorAsItem(obj.getBoolean("indicatorAsItem"));
									}
//								c1.setInspectPlanItemName(obj
//										.getString("inspectPlanItemName"));
//								c1.setInspectPlanItemDescrible(obj
//										.getString("inspectPlanItemDescrible"));
//								c1.setInspectPlanItemReferencePrefix(obj
//										.getString("inspectPlanItemReferencePrefix"));
//								c1.setInspectPlanItemReferenceSubfix(obj
//										.getString("inspectPlanItemReferenceSubfix"));
//								c1.setInspectPlanItemUnit(obj
//										.getString("inspectPlanItemUnit"));
//								c1.setItemConditionDescrible(obj
//										.getString("itemConditionDescrible"));
//								c1.setModelId(obj.getString("modelId"));
//								c1.setResourceId(obj.getLong("resourceId"));
//								c1.setIndexModelId(obj
//										.getString("indexModelId"));
//								c1.setType(c.getType());
//								c1.setIndicatorAsItem(obj
//										.getBoolean("indicatorAsItem"));
									c1.setSort(i);
									
								}
							}
						}
					}
				}
				this.inspectPlanDao.addItems(contensData);
			}
		}
		return 1;
	}

	@Override
	public List<InspectPlanContentBo> loadItemHierarchyTwo(long catalogId) {
		return this.inspectPlanDao.loadItemHierarchyTwo(catalogId);
	}
	
	public String ClobToString(Clob clob) throws SQLException, IOException {

		String reString = "";
		Reader is = clob.getCharacterStream();// 得到流
		BufferedReader br = new BufferedReader(is);
		String s = br.readLine();
		StringBuffer sb = new StringBuffer();
		while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
		sb.append(s);
		s = br.readLine();
		}
		reString = sb.toString();
		return reString;
		}

	@Override
	public int saveBasic(long inspectid, long domainid) {
		InspectPlanBasicBo basicBo= inspectPlanDao.get(inspectid, domainid);
		if(basicBo!=null){
			return 1;
		}
		Long id= this.sequence.next();
		inspectPlanDao.saveDomainBasic(id,inspectid,domainid);
		return 0;
	}

	@Override
	public List<InspectPlanBasicBo> inspectPlanloadAll(long id) {
		// TODO Auto-generated method stub
		return inspectPlanDao.inspectPlanloadAll(id);
	}

	@Override
	public InspectPlanBasicBo get(long inspectid, long domainid) {
		// TODO Auto-generated method stub
		return inspectPlanDao.get(inspectid, domainid);
	}

	@Override
	public int delItems(Long id,List<Long> items) {
		// TODO Auto-generated method stub
		return inspectPlanDao.del(id,items);
	}

	@Override
	public List<InspectDomainRole> getDomainRoleByUserId(Long id, Long planId) {
		List<DomainRole> role = new ArrayList<DomainRole>();
		List<InspectDomainRole> roles = new ArrayList<InspectDomainRole>();
	if(id!=0){
		User inspectUser = userApi.get(id);
		
		if(id==1 || inspectUser.getUserType() == 3){//admin或者系统管理员
			
			List<Domain> domains = this.domainApi.getAllDomains();
			if(domains!=null){
				for(int i=0;i<domains.size();i++){
					InspectDomainRole insrole = new InspectDomainRole();
					insrole.setRoleName(null);
					insrole.setRoles(null);
					insrole.setId(domains.get(i).getId());
					insrole.setName(domains.get(i).getName());
					roles.add(insrole);
					
				}
			}
		}else{
			 role=	userApi.getDomainRoleByUserId(id);
				for (DomainRole r : role) {
					InspectDomainRole insrole = new InspectDomainRole();
					insrole.setRoleName(r.getRoleName());
					insrole.setRoles(r.getRoles());
					insrole.setId(r.getId());
					insrole.setName(r.getName());
					roles.add(insrole);
					
				}
		}
	}
	
	

	if(roles!=null && roles.size()!=0){
		if(planId==null){//新增
			return roles;
		}else{
			BasicInfoBo bo= inspectPlanDao.loadBasic(planId);
			if(bo!=null){
				if(id==Long.parseLong(bo.getInspectPlanInspector())){//同个巡检
					String domains	=bo.getInspectPlanDomain();
					if(domains!=null){
						String[] domArr=domains.split(",");
						for(int i=0;i<domArr.length;i++){
							for (int j=0;j<roles.size();j++) {
								if(roles.get(j).getId()==Long.parseLong(domArr[i])){
									roles.get(j).setChecked("true");
								}
							}
						}
					}	
				}else{
					
				}
		
			
			}	
		}
	
	}
		// TODO Auto-generated method stub
		return roles;
	}
	
	public boolean isShowMerticValue(ProfileMetric metric,CustomMetricInfo info, long collectTime){
		logger.info("insplanNum "+insplanNum);
		String monitorFeq=null;
		long monitorFeqTime=0L;
		String monitorFeqUnit=null;
		if(info!=null){//自定义指标
		 monitorFeq=info.getFreq().name();
		/* System.out.println(info.getFreq().getFreq());
		 System.out.println(info.getFreq().getUnit());*/
		 monitorFeqTime=Long.parseLong(monitorFeq.replaceAll("[a-zA-Z]", ""));
		 monitorFeqUnit=monitorFeq.replaceAll("[0-9]", "");
	}else{
		 monitorFeq=	metric.getDictFrequencyId();
		 monitorFeqTime=Long.parseLong(monitorFeq.replaceAll("[a-zA-Z]", ""));
		 monitorFeqUnit=monitorFeq.replaceAll("[0-9]", "");
	}
//		String monitorFeq=	metric.getDictFrequencyId();
	
	Boolean isShow=true;
	logger.info("unit: "+monitorFeqUnit);
	logger.info("采集时间："+collectTime);
	long current=System.currentTimeMillis();//当前值
	Long time=monitorFeqTime*insplanNum;
	if(monitorFeqUnit.equals("min")){//分钟
	/*	System.out.println("======MIN=====");
		System.out.println("current: "+current);
		System.out.println("collectTime: "+collectTime);
		System.out.println("end: "+(current-collectTime)/1000/60);*/
		if((current-collectTime)/1000/60>time){
			isShow=false;
		}
	}else if(monitorFeqUnit.equals("hour")){//小时
	/*	System.out.println("======HOUR=====");
		System.out.println("current: "+current);
		System.out.println("collectTime: "+collectTime);
		System.out.println("end: "+(current-collectTime)/1000/60/60);*/
		if((current-collectTime)/1000/60/60>time){
			isShow=false;
		}
	}else{//天
	/*	System.out.println("======DAY=====");
		System.out.println("current: "+current);
		System.out.println("collectTime: "+collectTime);
		System.out.println("end: "+(current-collectTime)/1000/60/60/24);*/
		if((current-collectTime)/1000/60/60/24>time){
			isShow=false;
		}
	}
	logger.info("isShow: "+isShow);
		return isShow;
		
	}
}
