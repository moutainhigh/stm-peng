package com.mainsteam.stm.portal.inspect.web.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizCanvasApi;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.service.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.service.bo.BizInstanceNodeTree;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.portal.business.service.bo.BizNodeMetricRelBo;
import com.mainsteam.stm.portal.business.service.service.util.BizMetricDefine;
import com.mainsteam.stm.portal.inspect.api.IInspectPlanApi;
import com.mainsteam.stm.portal.inspect.api.IResourceApi;
import com.mainsteam.stm.portal.inspect.bo.BasicInfoBo;
import com.mainsteam.stm.portal.inspect.bo.Combotree;
import com.mainsteam.stm.portal.inspect.bo.InspectFrontReportOrPlanBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanContentBo;
import com.mainsteam.stm.portal.inspect.bo.ModelTree;
import com.mainsteam.stm.portal.inspect.bo.Option;
import com.mainsteam.stm.portal.inspect.bo.ReportResourceInstance;
import com.mainsteam.stm.portal.inspect.bo.ReportResourceMetric;
import com.mainsteam.stm.portal.inspect.bo.ResourceCategoryTree;
import com.mainsteam.stm.portal.inspect.bo.Routine;
import com.mainsteam.stm.portal.inspect.web.vo.InspectPlanBasicVo;
import com.mainsteam.stm.portal.inspect.web.vo.zTreeVo;
import com.mainsteam.stm.portal.resource.api.IResourceApplyApi;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.util.StringUtil;

@Controller
@RequestMapping("inspect/plan")
public class InspectPlanAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(InspectPlanAction.class);
	@Autowired
	private IInspectPlanApi inspectPlanApi;

	@Autowired
	private IResourceApi resourceApi;

	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private ResourceCategoryApi resourceCategoryApi;
	
	@Resource
	private CapacityService capacityService;
	
	
	@Autowired
	private BizMainApi bizMainApi;
	
	@Resource
	private BizCanvasApi bizCanvasApi;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource(name = "resourceDetailInfoApi")
	private IResourceDetailInfoApi resourceDetailInfoApi;
	
	@Resource
	private IResourceApplyApi resourceApplyApi;
	/** 
	 * 基本信息自定义显示项最大值
	 */
	@Value("${stm.inspect.basicMax}")
	private int basicMax;
	
	/**
	 * 巡检目录数最大值
	 */
	@Value("${stm.inspect.directoryMax}")
	private int directoryMax;
	
	/**
	 * 巡检项数最大值
	 */
	@Value("${stm.inspect.itemMax}")
	private int itemMax;
	
	/**
	 * 结论数最大值
	 */
	@Value("${stm.inspect.resultMax}")
	private int resultMax;


	@RequestMapping("list")
	public JSONObject list(
			long page,
			long rows,
			long startRow,
			long rowCount,
			@RequestParam(value = "condition.insepctPalnTaskName", required = false) String insepctPalnTaskName,
			@RequestParam(value = "condition.inspectPlanType", required = false) Integer[] inspectPlanTypes,
			@RequestParam(value = "condition.inspectPlanStatus", required = false) Integer[] inspectPlanStatus,
			@RequestParam(value = "condition.inspectPlanDomain", required = false) String[] inspectPlanDomains,
			@RequestParam(value = "condition.inspectPlanInspector", required = false) String inspectPlanInspector,
			@RequestParam(value = "condition.createUserName", required = false) String createUserName) {
		Page<InspectPlanBasicBo, InspectPlanBasicBo> p = new Page<InspectPlanBasicBo, InspectPlanBasicBo>();
		p.setRowCount(rowCount);
		p.setStartRow(startRow);
		InspectPlanBasicBo query = new InspectPlanBasicBo();
		p.setCondition(query);
		query.setInsepctPalnTaskName(insepctPalnTaskName);
		query.setInspectPlanTypes(inspectPlanTypes);
		if (inspectPlanStatus != null && inspectPlanStatus.length == 1) {
			query.setInspectPlanStatus(inspectPlanStatus[0]);
		}
		query.setCreateUserName(createUserName);
		query.setInspectPlanDomains(inspectPlanDomains);
		query.setInspectorName(inspectPlanInspector);
		ILoginUser user = BaseAction.getLoginUser();
		query.setOrderUserId(user.getId() + "");
		if (!user.isSystemUser()) {
			query.setAuthorityUserId(user.getId() + "");
			if (user.isDomainUser()) {
				if (user.getDomainManageDomains() != null
						&& user.getDomainManageDomains().size() > 0) {
					String[] ds = new String[user.getDomainManageDomains()
							.size()];
					int index = 0;
					for (IDomain d : user.getDomainManageDomains()) {
						ds[index++] = d.getId() + "";
					}
					query.setAuthorityDomainIds(ds);
				}
			}
		}
		this.inspectPlanApi.list(p);
		return BaseAction.toSuccess(p);
	}

	@RequestMapping("updateBasic")
	public JSONObject updateBasic(InspectPlanBasicVo vo) throws ParseException {
		boolean change = "on".equals(vo.getInspectRlanReportEditable()) ? true
				: false;
		long id = -1;
		JSONArray array = new JSONArray();
		switch (vo.getInspectPlanType()) {
		case 1:// 手动

			break;
		case 2:// 每天(自动)
			JSONObject obj2 = new JSONObject();
			array.add(obj2);
			obj2.put("hour", v(vo.getInspectTypeHour()));
			obj2.put("minute", v(vo.getInspectTypeMinute()));
			break;
		case 3:// 每周(自动)
			StringBuilder week = new StringBuilder();
			if ("on".equals(vo.getInspectTypeMonday())) {
				week.append("MON");
			}
			if ("on".equals(vo.getInspectTypeTuesday())) {
				if (week.length() > 0) {
					week.append(",");
				}
				week.append("TUE");
			}
			if ("on".equals(vo.getInspectTypeWednesday())) {
				if (week.length() > 0) {
					week.append(",");
				}
				week.append("WED");
			}
			if ("on".equals(vo.getInspectTypeThursday())) {
				if (week.length() > 0) {
					week.append(",");
				}
				week.append("THU");
			}
			if ("on".equals(vo.getInspectTypeFriday())) {
				if (week.length() > 0) {
					week.append(",");
				}
				week.append("FRI");
			}
			if ("on".equals(vo.getInspectTypeSaturday())) {
				if (week.length() > 0) {
					week.append(",");
				}
				week.append("SAT");
			}
			if ("on".equals(vo.getInspectTypeSunday())) {
				if (week.length() > 0) {
					week.append(",");
				}
				week.append("SUN");
			}
			JSONObject obj3 = new JSONObject();
			array.add(obj3);
			obj3.put("week", week);
			obj3.put("hour", v(vo.getInspectTypeHour()));
			obj3.put("minute", v(vo.getInspectTypeMinute()));
			break;
		case 4:// 每月(自动)
			JSONObject obj4 = new JSONObject();
			array.add(obj4);
			obj4.put("day", vo.getInspectTypeDate());
			obj4.put("hour", v(vo.getInspectTypeHour()));
			obj4.put("minute", v(vo.getInspectTypeMinute()));
			break;
		case 5:// 自定义(自动)
			String dateStr = vo.getInspectCustomDate();
			if (!StringUtil.isNull(dateStr)) {
				String[] dates = dateStr.split(",");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				for (String d : dates) {
					Date date = sdf.parse(d);
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					JSONObject obj5 = new JSONObject();
					array.add(obj5);
					obj5.put("minute", v(cal.get(Calendar.MINUTE)));
					obj5.put("hour", v(cal.get(Calendar.HOUR_OF_DAY)));
					obj5.put("day", v(cal.get(Calendar.DATE)));
					obj5.put("month", v(cal.get(Calendar.MONTH) + 1));
					obj5.put("year", v(cal.get(Calendar.YEAR)));
				}
			}
			break;
		}
		if (vo.getInspectPlanInspector() == null) {
			vo.setInspectPlanInspector(BaseAction.getLoginUser().getId());
		}
		//保存新增巡检
		if (vo.getId() == null||vo.getId()==1) {//复制的巡检按照新增巡检来保存，复制的巡检页面传过来id=1
			id = this.inspectPlanApi.saveBasic(vo.getInsepctPalnTaskName(), vo
					.getInspectPlanType(), array.toJSONString(), vo
					.getInspectPlanDomain(), vo.getInspectPlanInspector(), vo
					.getInspectPlanDescrible(), change, BaseAction
					.getLoginUser().getId());
		
		} else {
			//判断是否为复制过来的
			BasicInfoBo ret = this.inspectPlanApi.loadBasic(vo.getId());
			if(ret!=null&&!ret.getInsepctPalnTaskName().equals(vo.getInsepctPalnTaskName())){//验证是编辑还是复制
//				System.out.println("111111111111");
				//保存复制巡检
			/*	id = this.inspectPlanApi.saveBasic(vo.getInsepctPalnTaskName(), vo
						.getInspectPlanType(), array.toJSONString(), vo
						.getInspectPlanDomain(), vo.getInspectPlanInspector(), vo
						.getInspectPlanDescrible(), change, BaseAction
						.getLoginUser().getId());*/
				this.inspectPlanApi.updateBasic(vo.getId(),
						vo.getInsepctPalnTaskName(), vo.getInspectPlanType(),
						array.toJSONString(), vo.getInspectPlanDomain(),
						vo.getInspectPlanInspector(), vo.getInspectPlanDescrible(),
						change);
				id = vo.getId();
			}else{
//				System.out.println("222");
				//编辑
				this.inspectPlanApi.updateBasic(vo.getId(),
						vo.getInsepctPalnTaskName(), vo.getInspectPlanType(),
						array.toJSONString(), vo.getInspectPlanDomain(),
						vo.getInspectPlanInspector(), vo.getInspectPlanDescrible(),
						change);
				id = vo.getId();
			}
		}
		return BaseAction.toSuccess(id);
	}

	private String v(String vStr) {
		if (vStr != null && !"".equals(vStr.trim())) {
			int v = Integer.parseInt(vStr);
			if (v >= 0 && v <= 9) {
				return "0" + v;
			}
			return v + "";
		}
		return "";
	}

	private String v(Integer v) {
		if (v != null) {
			if (v >= 0 && v <= 9) {
				return "0" + v;
			}
			return v + "";
		}
		return "";
	}

	@RequestMapping("updateState")
	public JSONObject updateState(long id, boolean state) {
		return BaseAction.toSuccess(this.inspectPlanApi.updateState(id, state));
	}

	/**
	 * 获取巡检人
	 * 
	 * @param domainId
	 *            域id
	 * @return
	 */
	@RequestMapping("getUser")
	public JSONObject getUser(long domainId) {
		List<Option> options = this.resourceApi.getUser(domainId);
		Option o = new Option();
		ILoginUser u = BaseAction.getLoginUser();
		o.setId(BaseAction.getLoginUser().getId() + "");
		o.setName(u.getName());
		if (!options.contains(o)) {
			options.add(o);
		}
		return BaseAction.toSuccess(options);
	}
	
	@RequestMapping("getAllUser")
	public JSONObject getAllUser() {
		List<Option> options=	resourceApi.getAllUser();
		return BaseAction.toSuccess(options);
//		resourceApi.get
//		o.setId(BaseAction.getLoginUser().getId() + "");
//		o.setName(u.getName());
//		if (!options.contains(o)) {
//			options.add(o);
//		}
//		return BaseAction.toSuccess(options);
	}

	/**
	 * 获取资源类型
	 * 
	 * @return
	 */
	@RequestMapping("getResources")
	public JSONObject getResources() {
		return BaseAction.toSuccess(this.resourceApi.getResources(BaseAction
				.getLoginUser().getId()));
	}

	/**
	 * 获取业务
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("getBusiness")
	public JSONObject getBusiness() throws Exception {
		return BaseAction.toSuccess(this.resourceApi.getBusiness(0));
	}

	@RequestMapping("updateRoutine")
	public JSONObject updateRoutine(Long id,
			String inspectPlanReportProduceTimeShow,
			String inspectPlanReportModifyTimeShow,
			String inspectPlanReportModifiorShow,
			String inspectReportResourceNameShow,
			String inspectReportBusinessNameShow,
			String inspectReportResourceName, String inspectReportBusinessName,
			String inspectReportResourceType,InspectFrontReportOrPlanBo requestBo) {
		boolean showReportDate = "on".equals(inspectPlanReportProduceTimeShow) ? true
				: false;// 报告生成时间
		boolean showReportModifiorDate = "on"
				.equals(inspectPlanReportModifyTimeShow) ? true : false;// 最后编辑时间
		boolean showReportModifior = "on".equals(inspectPlanReportModifiorShow) ? true
				: false;// 最后编辑人姓名
		boolean showReportReportResource = "on"
				.equals(inspectReportResourceNameShow) ? true : false;// 是否显示资源
		boolean showReportReportBusiness = "on"
				.equals(inspectReportBusinessNameShow) ? true : false;// 是否显示资源
		return BaseAction.toSuccess(this.inspectPlanApi.updateRoutine(id,
				showReportDate, showReportModifiorDate, showReportModifior,
				showReportReportResource, showReportReportBusiness,
				inspectReportResourceName, inspectReportBusinessName,
				inspectReportResourceType,requestBo));
	}

	@RequestMapping("updateInspectionItems")
	public JSONObject updateInspectionItems(Long basicId, Long[] id,
			String[] inspectPlanItemName, String[] inspectPlanItemDescrible,
			Long[] delId) {
		return BaseAction.toSuccess(this.inspectPlanApi.updateInspectionItems(
				basicId, id, inspectPlanItemName, inspectPlanItemDescrible,
				delId));
	}

	@RequestMapping("loadInspectionItems")
	public JSONObject loadInspectionItems(long id) {
		return BaseAction
				.toSuccess(this.inspectPlanApi.loadInspectionItems(id));
	}

	@RequestMapping("loadBasic")
	public JSONObject loadBasic(long id) {
		BasicInfoBo ret = this.inspectPlanApi.loadBasic(id);
		List<Option> users = new ArrayList<Option>();

		String domainId = ret.getInspectPlanDomain();
		if (null != domainId && !"".equals(domainId) && 0 != domainId.length()) {
			String[] domainIds=domainId.split(",");
			Long[] domainLongIds=new Long[domainIds.length];
			if(domainIds.length!=0){
				for (int i=0;i<domainIds.length;i++) {
					domainLongIds[i]=Long.parseLong(domainIds[i]);
				}
			}
			users = this.resourceApi.getAllUser();
		//	users = this.resourceApi.getUser(Long.parseLong(domainId));
			// 添加当前登录用户到巡检人列表中
			Option o = new Option();
			ILoginUser u = BaseAction.getLoginUser();
			o.setId(BaseAction.getLoginUser().getId() + "");
			o.setName(u.getName());

			if (!users.contains(o)) {
				users.add(o);
			}
		}
		ret.setUsers(users);

		return BaseAction.toSuccess(ret);
	}

	/**
	 * 添加关联巡检项及其子项里的资源类型
	 * 
	 * @return
	 */
	@RequestMapping("getModelTree")
	public JSONObject getModelTree() {
		List<ModelTree> list = this.resourceApi.getModelTree();
		List<Combotree> combotrees = new ArrayList<>();
		this.combotrees(list, combotrees, null, 1);
		return BaseAction.toSuccess(combotrees);
	}

	private void combotrees(List<ModelTree> list, List<Combotree> combotrees,
			String pid, int layer) {
		if (list != null) {
			for (ModelTree tree : list) {
				Combotree c = new Combotree();
				combotrees.add(c);
				c.setId(tree.getId());
				c.setName(tree.getName());
				c.setType(layer);
				c.setPid(pid);
				this.combotrees(tree.getChildModelTrees(), combotrees,
						c.getId(), layer + 1);
			}
		}
	}

	@RequestMapping("loadRoutine")
	public JSONObject loadRoutine(long id) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Routine routine = this.inspectPlanApi.getRoutine(id);
		List<Option> resources = this.resourceApi.getResources(BaseAction
				.getLoginUser().getId());
		List<Option> businesses = new ArrayList<Option>();
		try {
			businesses = this.resourceApi.getBusiness(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ret.put("routineInfo", routine);
		ret.put("resources", resources);
		ret.put("businesses", businesses);
		return BaseAction.toSuccess(routine);
	}

	@RequestMapping("loadRoutineWidthResourceBiz")
	public JSONObject loadRoutineWidthResourceBiz(long id) {
		Map<String, Object> ret = new HashMap<String, Object>();
		Routine routine = this.inspectPlanApi.getRoutine(id);
		List<ResourceCategoryTree> resources = this.resourceApi
				.getAllResourceCategoryIncludeVM(null);
        List<Option> businesses = new ArrayList<Option>();
		try {
			businesses = this.resourceApi.getBusiness(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//添加业务资源分类
        ResourceCategoryTree businessCate = new ResourceCategoryTree();
		businessCate.setId("Business");
		businessCate.setName("业务系统");
		businessCate.setPid("Resource");
		businessCate.setType(1);
		businessCate.setState("closed");
        resources.add(businessCate);

		ret.put("routineInfo", routine);
		ret.put("resources", resources);
		ret.put("businesses", businesses);
		return BaseAction.toSuccess(ret);
	}

	@RequestMapping("updateConclusion")
	public JSONObject updateConclusion(long id,InspectFrontReportOrPlanBo requestBo) {
		return BaseAction.toSuccess(this.inspectPlanApi.updateConclusion(id,requestBo));
	}

	@RequestMapping("loadConclusions")
	public JSONObject loadConclusions(long id) {
		return BaseAction.toSuccess(this.inspectPlanApi
				.getConclusionsByBasicId(id));
	}

	@RequestMapping("getResourceInspect")
	public JSONObject getResourceInspect(String modelId, String resourceId) {
		return BaseAction.toSuccess(this.resourceApi.getResourceInspect(
				modelId, resourceId));
	}

	@RequestMapping("getDomainsAllUser")
	public JSONObject getDomainsAllUser() {
		Set<IDomain> domains = BaseAction.getLoginUser().getDomains(
				ILoginUser.RIGHT_PLAN);
		Set<Option> data = new HashSet<>();
		if (domains != null) {
			for (IDomain d : domains) {
				data.addAll(this.resourceApi.getUser(d.getId()));
			}
		}
		return BaseAction.toSuccess(data);
	}

	@RequestMapping("getIndexModel")
	public JSONObject getIndexModel(String[] modelIds) {
		return BaseAction.toSuccess(this.resourceApi.getIndexModel(modelIds));
	}

	@RequestMapping("loadItem")
	public JSONObject loadItem(long catalogId) {
		List<InspectPlanContentBo> loadItemHierarchyTwo = this.inspectPlanApi.loadItemHierarchyTwo(catalogId);
		for(InspectPlanContentBo ipBo : loadItemHierarchyTwo){
			List<InspectPlanContentBo> children = ipBo.getChildren();
			if(children != null && children.size() > 0){
				for(InspectPlanContentBo cBo : children){
					List<InspectPlanContentBo> childLoadItemHierarchyTwo = this.inspectPlanApi.loadItemHierarchyTwo(cBo.getId());
					cBo.setChildren(childLoadItemHierarchyTwo);
				}
			}
		}
		return BaseAction.toSuccess(loadItemHierarchyTwo);
	}

	@RequestMapping("updateItem")
	public JSONObject updateItem(String json) {
		return BaseAction.toSuccess(this.inspectPlanApi.updateItem(json));
	}

	@RequestMapping("loadItemByOne")
	public JSONObject loadItemByOne(long catalogId) {
		List<InspectPlanContentBo> data = this.inspectPlanApi
				.loadItem(catalogId);
		return BaseAction.toSuccess(data);
	}

	@RequestMapping("updateItemBasic")
	public JSONObject updateItemBasic(String json) {
		return BaseAction.toSuccess(this.inspectPlanApi.updateItemBasic(json));
	}

	/**
	 * 修改巡检子项
	 * 
	 * @return
	 */
	@RequestMapping("updateItemChild")
	public JSONObject updateItemChild(String str) {
		return BaseAction.toSuccess(this.inspectPlanApi.updateItemChild(str));
	}

	@RequestMapping("exec")
	public JSONObject exec(final long id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				inspectPlanApi.saveExec(id);
			}
		}).start();
		return BaseAction.toSuccess(1);
	}

	/**
	 * 获取指标
	 */
	@RequestMapping("/getMetricListByResourceId")
	public JSONObject getMetricListByResourceId(String[] resourceIdList, String resType) {
		List<ReportResourceMetric> metricListByResource = null;
		if("Business".equals(resType)){
			metricListByResource = this.getBizMetricList();
		}else{
			metricListByResource = this.inspectPlanApi.getMetricListByResource(resourceIdList);
		}
		return BaseAction.toSuccess(metricListByResource);
	}

	@RequestMapping("delPlan")
	public JSONObject delPlan(long[] planIds) throws ClassNotFoundException,
			SchedulerException {
		return BaseAction.toSuccess(this.inspectPlanApi.delPlan(planIds));
	}

	@RequestMapping("delItems")
	public JSONObject delItems(long[] itemIds) {
		return BaseAction.toSuccess(this.inspectPlanApi.delItems(itemIds));
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("getReferenceValue")
	public JSONObject getReferenceValue(String resouceItem, String pointItem, String inspectType, Boolean checkBusinessnodeInformation)
			throws ProfilelibException, NumberFormatException, InstancelibException {
		JSONArray resouceItemArray = JSONArray.parseArray(resouceItem);
		JSONArray pointItemArray = JSONArray.parseArray(pointItem);
		Map<Object, List<JSONObject>> map = new LinkedHashMap<Object, List<JSONObject>>();
		for (int i = 0; i < resouceItemArray.size(); i++) {
			JSONObject resource = resouceItemArray.getJSONObject(i);
			List<JSONObject> pointList = new ArrayList<JSONObject>();
			for (int j = 0; j < pointItemArray.size(); j++) {
				JSONObject point = pointItemArray.getJSONObject(j);
				List<ProfileThreshold> list = this.profileService.getThresholdByInstanceIdAndMetricId(resource.getLongValue("id"),point.getString("id"));
				if (list != null) {
					for (ProfileThreshold p : list) {
						if (PerfMetricStateEnum.Normal == p
								.getPerfMetricStateEnum()) {
							String ep = p.getExpressionOperator();
							if (ep != null && ep.indexOf("<") >= 0) {
								point.put("inspectPlanItemReferenceSubfix",
										p.getThresholdValue());
							} else {
								point.put("inspectPlanItemReferencePrefix",
										p.getThresholdValue());// 前缀
							}
							break;
						}
					}
				}
				pointList.add(point);
			}
			if("Business".equals(resource.get("resourceId"))){
				resource.put("resourceName", "");
                for(JSONObject o : pointList){
                    String id = o.getString("id");
                    if("hostCapacity".equals(id) || "storageCapacity".equals(id) || "databaseCapacity".equals(id) || "bandwidthCapacity".equals(id)){
                        o.put("inspectPlanItemReferenceSubfix",70);
                    }
                }
			}
			map.put(resource, pointList);
			//logger.error("resource:  " + resource.toJSONString());
			logger.error("查询巡检类型：" + inspectType + " checkBusinessnodeInformation:" + checkBusinessnodeInformation + " resourceId:" + resource.getLongValue("id"));
			//判断是否是业务系统且是否显示所有子节点
			if("Business".equals(inspectType) && checkBusinessnodeInformation){
				//查询该业务系统下所有子节点
				List<BizCanvasNodeBo> nodeListByBiz = bizCanvasApi.getNodeListByBiz(resource.getLongValue("id"));
				for(BizCanvasNodeBo bn : nodeListByBiz){
					String menuType = null;
					switch(bn.getNodeType()){
					case 1:
						int nodeStatus = bn.getNodeStatus();
						if(nodeStatus == 1){
							menuType = "parentResource";
						}else if(nodeStatus == 2){
							menuType = "childResource";
						}else{
							menuType = "metricNode";
						}
						break;
					case 2:
						menuType = "childBiz";
						break;
					}
					logger.error("menuType：" + menuType);
					if("childBiz".equals(menuType)){	//业务系统
						JSONObject bizJB = (JSONObject)resource.clone();
						BizMainBo basicInfo = bizMainApi.getBasicInfo(bn.getInstanceId());
						bizJB.put("id", basicInfo.getId());
						bizJB.put("resourceName", "业务系统");
						bizJB.put("showName", basicInfo.getName());
						bizJB.put("parentId", resource.get("id"));
						List<ReportResourceMetric> bizMetricList = this.getBizMetricList();
						List<JSONObject> pList = new ArrayList<JSONObject>(bizMetricList.size());
						JSONObject me = null;
						for(ReportResourceMetric re : bizMetricList){
							me = new JSONObject();
							me.put("id", re.getId());
							me.put("name", re.getName());
							String style = "";
							if(MetricTypeEnum.PerformanceMetric == re.getStyle()){
								style = "性能指标";
							}else if(MetricTypeEnum.InformationMetric == re.getStyle()){
								style = "信息指标";
							}else {
								style = "可用性指标";
							}
							me.put("style", style);
							me.put("unit", re.getUnit());
							//获取前缀
							List<ProfileThreshold> merlist = this.profileService.getThresholdByInstanceIdAndMetricId(bizJB.getLongValue("id"),re.getId());
							if (merlist != null) {
								for (ProfileThreshold p : merlist) {
									if (PerfMetricStateEnum.Normal == p
											.getPerfMetricStateEnum()) {
										String ep = p.getExpressionOperator();
										if (ep != null && ep.indexOf("<") >= 0) {
											me.put("inspectPlanItemReferenceSubfix",
													p.getThresholdValue());
										} else {
											me.put("inspectPlanItemReferencePrefix",
													p.getThresholdValue());// 前缀
										}
										break;
									}
								}
							}
							pList.add(me);
						}
						map.put(bizJB, pList);
					}else{
						if("parentResource".equals(menuType)) {//是否是父资源
							JSONObject reChild = (JSONObject)resource.clone();
							Map<String, Object> resourceDetailInfo = resourceDetailInfoApi.getResourceDetailInfo(bn.getInstanceId());
							Map<String, Object> resourceInfo = resourceDetailInfoApi.getResourceInfo(bn.getInstanceId());
							Map<String, Object> objectMap = (Map<String, Object>)resourceDetailInfo.get("parent");
							String resourceName =  (String)objectMap.get("resourceName");
							String name =  (String)objectMap.get("name");
							String resourceId =  (String)objectMap.get("resourceId");
							ArrayList<Map<String, Object>> ipMap =  (ArrayList<Map<String, Object>>)objectMap.get("ip");
							String discoverIP = ipMap.size() == 0? "" : (String)ipMap.get(0).get("id");
							
							reChild.put("id", bn.getInstanceId());
							reChild.put("resourceName", resourceName);
							reChild.put("showName", bn.getShowName());
							reChild.put("resourceId", resourceId);
							reChild.put("discoverIP", discoverIP);
							reChild.put("parentId", resource.get("id"));
							String reLis = (String) resourceInfo.get("resourceId") + ";" + String.valueOf(bn.getInstanceId());
							String[] resourceIdList = new String[]{reLis};
							List<ReportResourceMetric> metricListByResource = this.inspectPlanApi.getMetricListByResource(resourceIdList);
							JSONObject me = null;
							List<JSONObject> pList = new ArrayList<JSONObject>(metricListByResource.size());
							for(ReportResourceMetric re : metricListByResource){
								me = new JSONObject();
								me.put("id", re.getId());
								me.put("name", re.getName());
								String style = "";
								if(MetricTypeEnum.PerformanceMetric == re.getStyle()){
									style = "性能指标";
								}else if(MetricTypeEnum.InformationMetric == re.getStyle()){
									style = "信息指标";
								}else {
									style = "可用性指标";
								}
								me.put("style", style);
								me.put("unit", re.getUnit());
								//获取前缀
								List<ProfileThreshold> merlist = this.profileService.getThresholdByInstanceIdAndMetricId(reChild.getLongValue("id"),re.getId());
								if (merlist != null) {
									for (ProfileThreshold p : merlist) {
										if (PerfMetricStateEnum.Normal == p
												.getPerfMetricStateEnum()) {
											String ep = p.getExpressionOperator();
											if (ep != null && ep.indexOf("<") >= 0) {
												me.put("inspectPlanItemReferenceSubfix",
														p.getThresholdValue());
											} else {
												me.put("inspectPlanItemReferencePrefix",
														p.getThresholdValue());// 前缀
											}
											break;
										}
									}
								}
								pList.add(me);
							}
							map.put(reChild, pList);
						}else if("childResource".equals(menuType)){//是否是子资源
							JSONObject reChild = (JSONObject)resource.clone();
							Map<String, Object> resourceDetailInfo = resourceDetailInfoApi.getResourceDetailInfo(bn.getInstanceId());
//							Map<String, Object> resourceInfo = resourceDetailInfoApi.getResourceInfo(bn.getInstanceId());
							Map<String, Object> objectMap = (Map<String, Object>)resourceDetailInfo.get("parent");
							String resourceName =  (String)objectMap.get("resourceName");
							String name =  (String)objectMap.get("name");
							String resourceId =  (String)objectMap.get("resourceId");
							ArrayList<Map<String, Object>> ipMap =  (ArrayList<Map<String, Object>>)objectMap.get("ip");
                            String discoverIP = ipMap.size() == 0? "" : (String)ipMap.get(0).get("id");
							
							reChild.put("id", bn.getInstanceId());
							reChild.put("resourceName", resourceName);
							reChild.put("showName", name);
							reChild.put("resourceId", resourceId);
							reChild.put("discoverIP", discoverIP);
							reChild.put("parentId", resource.get("id"));
							map.put(reChild, new ArrayList<JSONObject>());
							
							//获取当前子资源已选中指标
							List<BizInstanceNodeTree> childInstanceTree = bizCanvasApi.getSelectChildInstanceTree(bn.getId());
							for(BizInstanceNodeTree node : childInstanceTree) {
								List<BizInstanceNodeTree> children = node.getChildren();
								if(children != null && children.size() > 0) {
									for(BizInstanceNodeTree insNode : children) {
										JSONObject nodeChild = new JSONObject();
										String childResourceName =  insNode.getName();
										String childeName =  node.getName();
										
										nodeChild.put("id", Long.parseLong(insNode.getId()));
										nodeChild.put("resourceName", childeName);
										nodeChild.put("showName", childResourceName);
										nodeChild.put("resourceId", node.getId());
										nodeChild.put("discoverIP", discoverIP);
										nodeChild.put("parentId", reChild.get("id"));
										
										//查询子资源指标
										ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(Long.parseLong(insNode.getId()));
										ResourceMetricDef[] rmdArr = resourceApplyApi.getHeaderInfoByMetricType(bn.getInstanceId(), resourceInstance.getChildType());
										List<Map<String,String>> rmdList = new ArrayList<Map<String,String>>();
										for(ResourceMetricDef rmd:rmdArr){
											if("resourceNameAndState".equals(rmd.getId())) {
												continue;
											}
											Map<String,String> childMap = new HashMap<String,String>();
											childMap.put("metricId", rmd.getId());
											childMap.put("name", rmd.getName().toUpperCase());
											childMap.put("style", null==rmd.getMetricType()?"InformationMetric":rmd.getMetricType().name());
											childMap.put("unit", rmd.getUnit());
											rmdList.add(childMap);
										}
										List<JSONObject> pList = new ArrayList<JSONObject>(rmdList.size());
										JSONObject me = null;
										for(Map<String,String> reMap : rmdList){
											me = new JSONObject();
											me.put("id", (String)reMap.get("metricId"));
											me.put("name", (String)reMap.get("name"));
											String style = "";
											if(MetricTypeEnum.PerformanceMetric.toString().equals(reMap.get("style"))){
												style = "性能指标";
											}else if(MetricTypeEnum.InformationMetric.toString().equals(reMap.get("style"))){
												style = "信息指标";
											}else {
												style = "可用性指标";
											}
											me.put("style", style);
											me.put("unit", (String)reMap.get("unit"));
											//获取前缀
											List<ProfileThreshold> merlist = this.profileService.getThresholdByInstanceIdAndMetricId(nodeChild.getLongValue("id"),(String)reMap.get("metricId"));
											if (merlist != null) {
												for (ProfileThreshold p : merlist) {
													if (PerfMetricStateEnum.Normal == p
															.getPerfMetricStateEnum()) {
														String ep = p.getExpressionOperator();
														if (ep != null && ep.indexOf("<") >= 0) {
															me.put("inspectPlanItemReferenceSubfix",
																	p.getThresholdValue());
														} else {
															me.put("inspectPlanItemReferencePrefix",
																	p.getThresholdValue());// 前缀
														}
														break;
													}
												}
											}
											pList.add(me);
										}
										map.put(nodeChild, pList);
									}
								}
							}
							
						}else if("metricNode".equals(menuType)) {//是否是指定指标
							JSONObject reChild = (JSONObject)resource.clone();
							//获取当前子资源已选中指标
							List<BizInstanceNodeTree> instanceTree = bizCanvasApi.getSelectMetricTree(bn.getId());
							if(instanceTree.size() == 1 && instanceTree.get(0).getChildren() != null) {
								Map<String, Object> resourceDetailInfo = resourceDetailInfoApi.getResourceDetailInfo(bn.getInstanceId());
								Map<String, Object> objectMap = (Map<String, Object>)resourceDetailInfo.get("parent");
								List<Map<String, Object>> childrenMap = (List<Map<String, Object>>)resourceDetailInfo.get("childrenType");
								String resourceName =  (String)objectMap.get("resourceName");
								String resourceId = "";
								if(objectMap.get("instanceId").toString().equals(instanceTree.get(0).getId().toString())) {
									resourceId =  (String)objectMap.get("resourceId");
								}else {
									resourceId = (String)childrenMap.get(0).get("id");
								}
								ArrayList<Map<String, Object>> ipMap =  (ArrayList<Map<String, Object>>)objectMap.get("ip");
								String discoverIP = (String)ipMap.get(0).get("id");
								
								reChild.put("id", instanceTree.get(0).getId());	
								reChild.put("resourceName", resourceName);
								reChild.put("showName", bn.getShowName());
								reChild.put("resourceId", resourceId);
								reChild.put("discoverIP", discoverIP);
								reChild.put("parentId", resource.get("id"));
								JSONObject me = null;
								List<JSONObject> pList = new ArrayList<JSONObject>();
								//根据节点和实例ID查询该节点指标
								Map<Long, List<Map<String, String>>> metricInfoByInsIdAndNodeId = this.getMetricInfoByInsIdAndNodeId(bn.getInstanceId(), bn.getId());
								List<Map<String, String>> listMetric = metricInfoByInsIdAndNodeId.get(Long.parseLong(instanceTree.get(0).getId()));
								for(Map<String, String> metricInfoMap : listMetric) {
									me = new JSONObject();
									me.put("id", (String)metricInfoMap.get("metricId"));
									me.put("name", (String)metricInfoMap.get("metricName"));
									String style = (String)metricInfoMap.get("metricType");
									if(MetricTypeEnum.PerformanceMetric.toString().equals(style)){
										style = "性能指标";
									}else if(MetricTypeEnum.InformationMetric.toString().equals(style)){
										style = "信息指标";
									}else {
										style = "可用性指标";
									}
									me.put("style", style);
									me.put("unit", (String)metricInfoMap.get("metricUnit"));
									//获取前缀
									List<ProfileThreshold> merlist = this.profileService.getThresholdByInstanceIdAndMetricId(reChild.getLongValue("id"),(String)metricInfoMap.get("metricId"));
									if (merlist != null) {
										for (ProfileThreshold p : merlist) {
											if (PerfMetricStateEnum.Normal == p
													.getPerfMetricStateEnum()) {
												String ep = p.getExpressionOperator();
												if (ep != null && ep.indexOf("<") >= 0) {
													me.put("inspectPlanItemReferenceSubfix",
															p.getThresholdValue());
												} else {
													me.put("inspectPlanItemReferencePrefix",
															p.getThresholdValue());// 前缀
												}
												break;
											}
										}
										
									}
									pList.add(me);
								}		
								map.put(reChild, pList);
							}else {
								Map<String, Object> resourceDetailInfo = resourceDetailInfoApi.getResourceDetailInfo(bn.getInstanceId());
								Map<String, Object> resourceInfo = resourceDetailInfoApi.getResourceInfo(bn.getInstanceId());
								Map<String, Object> objectMap = (Map<String, Object>)resourceDetailInfo.get("parent");
								String resourceName =  (String)objectMap.get("resourceName");
								String name =  (String)objectMap.get("name");
								String resourceId =  (String)objectMap.get("resourceId");
								ArrayList<Map<String, Object>> ipMap =  (ArrayList<Map<String, Object>>)objectMap.get("ip");
                                String discoverIP = ipMap.size() == 0? "" : (String)ipMap.get(0).get("id");
								
								reChild.put("id", bn.getInstanceId());
								reChild.put("resourceName", resourceName);
								reChild.put("showName", bn.getShowName());
								reChild.put("resourceId", resourceId);
								reChild.put("discoverIP", discoverIP);
								reChild.put("parentId", resource.get("id"));
								String reLis = (String) resourceInfo.get("resourceId") + ";" + String.valueOf(bn.getInstanceId());
								String[] resourceIdList = new String[]{reLis};
								List<ReportResourceMetric> metricListByResource = this.inspectPlanApi.getMetricListByResource(resourceIdList);
								JSONObject me = null;
								List<JSONObject> pList = new ArrayList<JSONObject>(metricListByResource.size());
								
								for(ReportResourceMetric re : metricListByResource){
									for(BizInstanceNodeTree tree : instanceTree) {
										if(re.getId().equals(tree.getId())) {
											me = new JSONObject();
											me.put("id", re.getId());
											me.put("name", re.getName());
											String style = "";
											if(MetricTypeEnum.PerformanceMetric == re.getStyle()){
												style = "性能指标";
											}else if(MetricTypeEnum.InformationMetric == re.getStyle()){
												style = "信息指标";
											}else {
												style = "可用性指标";
											}
											me.put("style", style);
											me.put("unit", re.getUnit());
											//获取前缀
											List<ProfileThreshold> merlist = this.profileService.getThresholdByInstanceIdAndMetricId(reChild.getLongValue("id"),re.getId());
											if (merlist != null) {
												for (ProfileThreshold p : merlist) {
													if (PerfMetricStateEnum.Normal == p
															.getPerfMetricStateEnum()) {
														String ep = p.getExpressionOperator();
														if (ep != null && ep.indexOf("<") >= 0) {
															me.put("inspectPlanItemReferenceSubfix",
																	p.getThresholdValue());
														} else {
															me.put("inspectPlanItemReferencePrefix",
																	p.getThresholdValue());// 前缀
														}
														break;
													}
												}
											}
											pList.add(me);
										}
									}
								}
								map.put(reChild, pList);
							}
						}else {
							continue;
						}
						
					}
					
				}
			}
		}
		return BaseAction.toSuccess(map);
	}

	@RequestMapping("copy")
	public JSONObject copy(long id, HttpSession session) {
		return BaseAction.toSuccess(this.inspectPlanApi.saveCopy(id,getLoginUser(session).getId()));
	}

	@RequestMapping("updateReportName")
	public JSONObject updateReportName(long planId, String name) {
		return BaseAction.toSuccess(this.inspectPlanApi.updateReportName(
				planId, name));
	}

	@RequestMapping("updateItems")
	public JSONObject updateItems(long dirId, String json) {
		return BaseAction.toSuccess(this.inspectPlanApi
				.updateItems(dirId, json));
	}

	/**
	 * 获取资源类型
	 */
	@RequestMapping("/getResourceCategoryList")
	public JSONObject getResourceCategoryList(String id,String domainId) {
		List<ResourceCategoryTree> categorys = this.resourceApi.getAllResourceCategory(id);
		List<ResourceCategoryTree> rcs=getTreeList(categorys, domainId);
		return BaseAction.toSuccess(rcs);

	}
	
	/**
	 * 自动巡检获取资源类型
	 */
	@RequestMapping("/getResourceCategoryListByInspection")
	public JSONObject getResourceCategoryListByInspection(String id,String domainId) {
		List<ResourceCategoryTree> categorys = this.resourceApi.getAllResourceCategoryByInspection(id);
		List<ResourceCategoryTree> rcs = this.getTreeList2(categorys, domainId);
		return BaseAction.toSuccess(rcs);

	}

	//巡检增加虚拟化资源选项列表
    private List<ResourceCategoryTree> getTreeList2(List<ResourceCategoryTree> categorys, String domainId){
        List<ResourceCategoryTree> tress = new ArrayList<ResourceCategoryTree>();
        List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi.getAllResourceInstanceList(BaseAction.getLoginUser());

        //域过滤 dfw 20170123
        List<ResourceInstanceBo> newResourceInstanceList = null;
        if (!CollectionUtils.isEmpty(resourceInstanceList)) {
            //如果域参数为空，则认为返回所有数据
            if (StringUtil.isNull(domainId)) {
                newResourceInstanceList = resourceInstanceList;
            } else {
                newResourceInstanceList = new ArrayList<ResourceInstanceBo>();
                List<String> list = Arrays.asList(domainId.split(","));
                for (ResourceInstanceBo item : resourceInstanceList) {
                    if (list.contains(String.valueOf(item.getDomainId()))) {
                        newResourceInstanceList.add(item);
                    }
                }
            }
        }

        List<zTreeVo> clist = this.getTreeListByResourceInstanceList(newResourceInstanceList, true);
        //获取虚拟化分类
        List<ResourceCategoryTree> vmCategory = this.resourceApi.getAllResourceCategory("VM");
        List<ResourceCategoryTree> vmList = new ArrayList<ResourceCategoryTree>(vmCategory.size());
        for(int i = 0; i < vmCategory.size(); ++i){
            ResourceCategoryTree resourceCategoryTree = vmCategory.get(i);
            if("VM".equals(resourceCategoryTree.getPid())){
                vmList.add(resourceCategoryTree);
            }
        }
        //组装虚拟化#####
        zTreeVo vmZt = new zTreeVo();
        vmZt.setId("VM");
        vmZt.setIsParent(true);
        vmZt.setName("虚拟化资源");
        vmZt.setNocheck(false);
        vmZt.setOpen(false);
        vmZt.setPId("0");
        vmZt.setChecked(false);
        List<zTreeVo> list = new ArrayList<zTreeVo>(clist.size());
        list.add(vmZt);
        for(zTreeVo zv : clist){
            list.add(zv);
            for(ResourceCategoryTree rt : vmList){
                if(zv.getId().equals(rt.getId())){
                    List<zTreeVo> children = vmZt.getChildren();
                    if(children == null){
                        children = new ArrayList<zTreeVo>(vmList.size());
                    }
                    zv.setPId("VM");
                    children.add(zv);
                    vmZt.setChildren(children);
                    list.remove(zv);
                }
            }
        }
        //#################end
        for (int j = 0; j < list.size(); j++) {
            for (int i = 0; i < categorys.size(); i++) {
                if (categorys.get(i).getType() == 1) {//第一级
                    if (list.get(j).getPId().equals("0")) {//第一级
                        if (categorys.get(i).getId().equals(list.get(j).getId())) {
                            tress.add(categorys.get(i));

                        } else {
                            List<zTreeVo> vos = list.get(j).getChildren();
                            if (vos != null && vos.size() != 0) {
                                for (int k = 0; k < vos.size(); k++) {
                                    if (vos.get(k).getId().equals(categorys.get(i).getId())) {
                                        tress.add(categorys.get(i));
                                    }
                                }
                            }
                        }
                    }
                } else {
                    List<zTreeVo> vos = list.get(j).getChildren();
                    if (vos != null && vos.size() != 0) {
                        for (int k = 0; k < vos.size(); k++) {
                            if (vos.get(k).getId().equals(categorys.get(i).getPid())) {
                                tress.add(categorys.get(i));
                            }
                        }
                    }
                }
            }
        }
        //添加虚拟化三级节点
        if(vmZt.getChildren() != null && vmZt.getChildren().size() > 0){
            for(zTreeVo zv : vmZt.getChildren()){
                if(zv.getChildren().size() > 0){
                    for(zTreeVo cz : zv.getChildren()){
                        for(ResourceCategoryTree rt : categorys){
                            if(cz.getId().equals(rt.getId()) || cz.getId().equals(rt.getPid())){
                                tress.add(rt);
                            }
                        }
                    }
                }
            }
        }


        return tress;
    }

    //添加虚拟化

    // 通过resourceInstance列表构建树结构包含二级和三级结构
    private List<zTreeVo> getTreeListByResourceInstanceList2(List<ResourceInstanceBo> instanceList, boolean isFilterDisplay) {

        List<zTreeVo> treeOneList = new ArrayList<zTreeVo>();
        List<zTreeVo> treeTwoList = new ArrayList<zTreeVo>();
        List<zTreeVo> treeThreeList = new ArrayList<zTreeVo>();

        Map<String, List<zTreeVo>> threeTreeAndInstanceMap = new HashMap<String, List<zTreeVo>>();

        Map<String, List<zTreeVo>> secondTreeAndInstanceMap = new HashMap<String, List<zTreeVo>>();

        Map<String, List<zTreeVo>> firstTreeAndSecondTreeMap = new HashMap<String, List<zTreeVo>>();

        if (instanceList == null || instanceList.size() <= 0) {
            return treeOneList;
        }
        for (int i = 0; i < instanceList.size(); i++) {
            ResourceInstanceBo resourceInstance = instanceList.get(i);
            if (resourceInstance.getCategoryId() == null) {
                if (logger.isErrorEnabled()) {
                    logger.error("resourceInstance getCategoryId is null ,resourceId : " + resourceInstance.getId());
                }
            }

            // 根据资源实例ID获取类别
            CategoryDef secondCategory = capacityService.getCategoryById(resourceInstance.getCategoryId());
            if (secondCategory == null) {
                if (logger.isErrorEnabled()) {
                    logger.error("capacityService.getCategoryById() error,id = " + resourceInstance.getCategoryId());
                }
                continue;
            }
            // 判断是一级还是二级类别
            CategoryDef firstCategory = secondCategory.getParentCategory();
            CategoryDef threeCategory = null;
            zTreeVo instanceTree = null;
            zTreeVo threeTree = null;
            zTreeVo secondTree = null;
            zTreeVo firstTree = null;
            if(!"Resourcw".equals(firstCategory.getParentCategory().getId())){  //二级类别
                CategoryDef temp = firstCategory;
                firstCategory = firstCategory.getParentCategory();
                threeCategory = secondCategory;
                secondCategory = temp;

                instanceTree = this.defTozTreeVo(resourceInstance, false,threeCategory.getId());
                threeTree = this.defTozTreeVo(threeCategory, true,secondCategory.getId());
                secondTree = this.defTozTreeVo(secondCategory, true,firstCategory.getId());
                firstTree = this.defTozTreeVo(firstCategory, true, "0");

            }else{
                instanceTree = this.defTozTreeVo(resourceInstance, false,secondCategory.getId());
                secondTree = this.defTozTreeVo(secondCategory, true,firstCategory.getId());
                firstTree = this.defTozTreeVo(firstCategory, true, "0");
            }

            // 判断threeTreeAndInstanceMap中是否存在三级类别ID的数据
            if (!threeTreeAndInstanceMap.containsKey(threeTree.getId())) {
                threeTreeAndInstanceMap.put(threeTree.getId(),new ArrayList<zTreeVo>());
            }
            threeTreeAndInstanceMap.get(threeTree.getId()).add(instanceTree);

            // 判断secondTreeAndInstanceMap中是否存在二级类别ID的数据
            if (!secondTreeAndInstanceMap.containsKey(secondTree.getId())) {
                secondTreeAndInstanceMap.put(secondTree.getId(),new ArrayList<zTreeVo>());
            }
            secondTreeAndInstanceMap.get(secondTree.getId()).add(instanceTree);

            if (!treeThreeList.contains(threeTree)) {
                treeThreeList.add(threeTree);
            }
            if (!treeTwoList.contains(secondTree)) {
                treeTwoList.add(secondTree);
            }
            if (!treeOneList.contains(firstTree)) {
                treeOneList.add(firstTree);
            }

        }
        //构建三级树
        for (zTreeVo threeTree : treeThreeList) {
            threeTree.setChildren(secondTreeAndInstanceMap.get(threeTree.getId()));
            if (!secondTreeAndInstanceMap.containsKey(threeTree.getPId())) {
                secondTreeAndInstanceMap.put(threeTree.getPId(), new ArrayList<zTreeVo>());
            }
            secondTreeAndInstanceMap.get(threeTree.getPId()).add(threeTree);
        }

        // 构建二级树
        for (zTreeVo secondTree : treeTwoList) {
            secondTree.setChildren(secondTreeAndInstanceMap.get(secondTree.getId()));
            if (!firstTreeAndSecondTreeMap.containsKey(secondTree.getPId())) {
                firstTreeAndSecondTreeMap.put(secondTree.getPId(), new ArrayList<zTreeVo>());
            }
            firstTreeAndSecondTreeMap.get(secondTree.getPId()).add(secondTree);
        }

        // 构建一级树
        for (zTreeVo firstTree : treeOneList) {
            firstTree.setChildren(firstTreeAndSecondTreeMap.get(firstTree .getId()));
        }
        return treeOneList;
    }


	public List<ResourceCategoryTree>  getTreeList(List<ResourceCategoryTree> categorys, String domainId){
		List<ResourceCategoryTree> tress= new ArrayList<ResourceCategoryTree>();
		List<ResourceInstanceBo> resourceInstanceList= resourceCategoryApi.getAllResourceInstanceList(BaseAction.getLoginUser());
		
		//域过滤 dfw 20170123
		List<ResourceInstanceBo> newResourceInstanceList = null;
		if(!CollectionUtils.isEmpty(resourceInstanceList)){
			//如果域参数为空，则认为返回所有数据
			if(StringUtil.isNull(domainId)){
				newResourceInstanceList = resourceInstanceList;
			} else{
				newResourceInstanceList = new ArrayList<ResourceInstanceBo>();
				List<String> list = Arrays.asList(domainId.split(","));
				for(ResourceInstanceBo item : resourceInstanceList){
					if(list.contains(String.valueOf(item.getDomainId()))){
						newResourceInstanceList.add(item);
					}
				}
			}
		}
	
	 List<zTreeVo> list= getTreeListByResourceInstanceList(newResourceInstanceList, true);
	 for(int j=0;j<list.size();j++){
	 for(int i=0;i<categorys.size();i++){
			 if(categorys.get(i).getType()==1){//第一级
				 if(list.get(j).getPId().equals("0")){//第一级
					 if(categorys.get(i).getId().equals(list.get(j).getId())){
						 tress.add(categorys.get(i)) ;
						 
					 }else{
						 List<zTreeVo> vos=list.get(j).getChildren();
						 if(list.get(j).getChildren().size()!=0){
							 for(int k=0;k<vos.size();k++){
							 if(vos.get(k).getId().equals(categorys.get(i).getId())){
								 tress.add(categorys.get(i)); 
							 }
						 }
						 }
					 }
				 }
			 }else{
				 List<zTreeVo> vos=list.get(j).getChildren();
				 if(list.get(j).getChildren().size()!=0){
					 for(int k=0;k<vos.size();k++){
					 if(vos.get(k).getId().equals(categorys.get(i).getPid())){
						 tress.add(categorys.get(i)); 
					 }
				 }
				 }
				}
		 }
	 }
	return tress;
		 
	 }
		// 通过resourceInstance列表构建树结构(两级类别，一级资源)
		private List<zTreeVo> getTreeListByResourceInstanceList(List<ResourceInstanceBo> instanceList, boolean isFilterDisplay) {

			List<zTreeVo> treeOneList = new ArrayList<zTreeVo>();
			List<zTreeVo> treeTwoList = new ArrayList<zTreeVo>();

			Map<String, List<zTreeVo>> secondTreeAndInstanceMap = new HashMap<String, List<zTreeVo>>();

			Map<String, List<zTreeVo>> firstTreeAndSecondTreeMap = new HashMap<String, List<zTreeVo>>();

			if (instanceList == null || instanceList.size() <= 0) {
				return treeOneList;
			}
			for (int i = 0; i < instanceList.size(); i++) {

				ResourceInstanceBo resourceInstance = instanceList.get(i);
				if (resourceInstance.getCategoryId() == null) {
					if(logger.isErrorEnabled()){
						logger.error("resourceInstance getCategoryId is null ,resourceId : " + resourceInstance.getId());
					}
				}

				// 根据资源实例ID获取二级类别
				CategoryDef secondCategory = capacityService
						.getCategoryById(resourceInstance.getCategoryId());
				// 根据一级类别获取二级类别
				if(secondCategory == null){
					if(logger.isErrorEnabled()){
						logger.error("capacityService.getCategoryById() error,id = " + resourceInstance.getCategoryId()); 
					}
					continue;
				}
				CategoryDef firstCategory = secondCategory.getParentCategory();
				
				//过滤虚拟化资源
//				if(!DefIsdisplay(firstCategory) && isFilterDisplay) {
//					continue;
//				}
				zTreeVo instanceTree = this.defTozTreeVo(resourceInstance, false,
						secondCategory.getId());

				zTreeVo secondTree = this.defTozTreeVo(secondCategory, true,
						firstCategory.getId());

				zTreeVo firstTree = this.defTozTreeVo(firstCategory, true, "0");

				// 判断secondTreeAndInstanceMap中是否存在二级类别ID的数据
				if (!secondTreeAndInstanceMap.containsKey(secondTree.getId())) {

					secondTreeAndInstanceMap.put(secondTree.getId(),
							new ArrayList<zTreeVo>());

				}

				secondTreeAndInstanceMap.get(secondTree.getId()).add(instanceTree);

				if (!treeTwoList.contains(secondTree)) {

					treeTwoList.add(secondTree);

				}

				if (!treeOneList.contains(firstTree)) {
					treeOneList.add(firstTree);
				}

			}

			// 构建二级树
			for (zTreeVo secondTree : treeTwoList) {

				secondTree.setChildren(secondTreeAndInstanceMap.get(secondTree
						.getId()));

				if (!firstTreeAndSecondTreeMap.containsKey(secondTree.getPId())) {

					firstTreeAndSecondTreeMap.put(secondTree.getPId(),
							new ArrayList<zTreeVo>());

				}

				firstTreeAndSecondTreeMap.get(secondTree.getPId()).add(secondTree);

			}

			// 构建一级树
			for (zTreeVo firstTree : treeOneList) {

				firstTree.setChildren(firstTreeAndSecondTreeMap.get(firstTree
						.getId()));

			}

			return treeOneList;

		}
	/**
	 * 获取子资源类型根据类别或者主资源
	 */
	@RequestMapping("/getChildResourceListByMainResourceOrCategory")
	public JSONObject getChildResourceListByMainResourceOrCategory(
			ResourceCategoryTree category) {
		List<ResourceCategoryTree> defs = new ArrayList<ResourceCategoryTree>();
		if("Business".equals(category.getId())){
			return BaseAction.toSuccess("Business");
		}else{
			if (category.getType() == 1) {
				// 类别获取子资源
				defs = resourceApi.getChildResourceByResourceCategory(category
						.getId());
			} else if (category.getType() == 2) {
				// 主资源获取子资源
				defs = resourceApi.getChildResourceByMainResource(category.getId());
			}
			return BaseAction.toSuccess(defs);
		}
	}

	/**
	 * 获取资源实例
	 * 4.2 一个人员多个域
	 */
	@RequestMapping("/getResourceInstanceList")
	public JSONObject getResourceInstanceList(String queryId, int type,	String domainId, int startNum, int pageSize, HttpSession session) {
		List<ReportResourceInstance> defs = new ArrayList<ReportResourceInstance>();
		List<Long> domainIds= new ArrayList<Long>();
		if(domainId!=null ||domainId!=""){
			String[] ids= domainId.split(",");
			if(ids.length!=0){
				for (String id : ids) {
					domainIds.add(Long.parseLong(id));
				}
			}
		}
		if("Business".equals(queryId)){
			defs = this.getBizInsByDomainId(session, domainIds);
		}else{
			if (type == 1) {
				// 通过类别获取资源实例
				defs = resourceApi.getResourceInstanceByCategoryId(queryId,
						domainIds, getLoginUser(session));
			} else if (type == 2) {
				// 通过资源获取资源实例
				defs = resourceApi.getInstanceByResource(queryId, domainIds,
						getLoginUser(session));
			}
		}
		
		if((startNum + pageSize) > defs.size()){
			defs = defs.subList(startNum, defs.size());
		}else{
			defs = defs.subList(startNum, (startNum + pageSize));
		}
		
		return toSuccess(defs);
	}

	/**
	 * 过滤资源实例
	 */
	@RequestMapping("/filterInstanceInfoByContent")
	public JSONObject filterInstanceInfoByContent(String instanceIds,
			String content,boolean businessFlag,String domainId,HttpSession session) {
		List<ReportResourceInstance> defs = new ArrayList<ReportResourceInstance>();
		if(businessFlag){
			List<Long> domainIds= new ArrayList<Long>();
			if(domainId!=null ||domainId!=""){
				String[] idList = domainId.split(",");
				if(idList.length!=0){
					for (String id : idList) {
						domainIds.add(Long.parseLong(id));
					}
				}
			}
			defs = this.getBizInsByDomainId(session, domainIds);
			if(!StringUtil.isNull(content)){
				List<ReportResourceInstance> tempList = new ArrayList<ReportResourceInstance>();
				for(ReportResourceInstance re : defs){
					if(re.getShowName().indexOf(content.trim()) != -1 || (re.getResourceName() != null && re.getResourceName().indexOf(content.trim()) != -1)){
						tempList.add(re);
					}
				}
				defs = tempList;
			}
		}else{
			List<Long> ids = new ArrayList<Long>();
			if (instanceIds.contains(",")) {
				for (String id : instanceIds.split(",")) {
					ids.add(Long.parseLong(id));
				}
			} else {
				ids.add(Long.parseLong(instanceIds));
			}
			defs = resourceApi.filterResourceInstanceList(ids, content);
		}
		return toSuccess(defs);
	}
	
	/**
	 * 获取巡检相关配置属性
	 */
	@RequestMapping("/getInspectProperties")
	public JSONObject getInspectProperties() {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		resultMap.put("basicMax", this.basicMax);
		resultMap.put("directoryMax", this.directoryMax);
		resultMap.put("itemMax", this.itemMax);
		resultMap.put("resultMax", this.resultMax);
		return toSuccess(resultMap);
	}
	
	@RequestMapping(value="/getDomainRoles")
	public JSONObject getDomainRoles(Long id, Long planId, HttpSession session){
		return toSuccess(inspectPlanApi.getDomainRoleByUserId(id,planId));
	}
	
	private zTreeVo defTozTreeVo(Object def, boolean isParent, String pid) {

		zTreeVo tree = new zTreeVo();

		if (def instanceof ResourceInstanceBo) {

			ResourceInstanceBo instance = (ResourceInstanceBo) def;
			tree.setId(instance.getId() + "");
			tree.setIsParent(isParent);
			String showName = "";
			if(instance.getShowName() != null){
				showName = instance.getShowName();
			}
			String ip = "";
			if(instance.getDiscoverIP() != null){
				ip = instance.getDiscoverIP();
			}
			tree.setName("("+ip + ")" + showName );
			tree.setNocheck(false);
			tree.setOpen(false);
			tree.setPId(pid);

		} else if (def instanceof CategoryDef) {

			CategoryDef instance = (CategoryDef) def;
			tree.setId(instance.getId());
			tree.setIsParent(isParent);
			tree.setName(instance.getName());
			tree.setNocheck(false);
			tree.setOpen(false);
			tree.setPId(pid);

		}

		return tree;

	}
	private boolean DefIsdisplay(CategoryDef def){
		if(!def.isDisplay()){
			return false;
		}else{
			if(def.getParentCategory() != null){
				return DefIsdisplay(def.getParentCategory());
			}else{
				return true;
			}
		}
	}
	
	private List<ReportResourceInstance> getBizInsByDomainId(HttpSession session,List<Long> domainIds){
		
		List<BizMainBo> bizList = bizMainApi.getAllViewBiz(getLoginUser(session));
		List<ReportResourceInstance> defs = new ArrayList<ReportResourceInstance>();
		List<BizMainBo> bList = new ArrayList<BizMainBo>();
		
		for(BizMainBo bz : bizList){
			for(long id : domainIds){
				if(bz.getDomainId() == id){
					bList.add(bz);
					break;
				}
			}
		}
		ReportResourceInstance rri = null;
		for(BizMainBo bz : bList){
			rri = new ReportResourceInstance();
			rri.setId(bz.getId());
			rri.setShowName(bz.getName());
			rri.setResourceName(bz.getManagerName());
			rri.setResourceId("Business");
			defs.add(rri);
		}
		
		return defs;
	}
	
	private List<ReportResourceMetric> getBizMetricList(){
		List<ReportResourceMetric> rList = new ArrayList<ReportResourceMetric>();
		ReportResourceMetric rrm = null;
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.BIZ_HEALTH_STATUS);
		rrm.setName(BizMetricDefine.BIZ_HEALTH_STATUS_NAME);
		rrm.setUnit("分");
		rrm.setStyle(MetricTypeEnum.PerformanceMetric);
		rList.add(rrm);
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.AVAILAVLE_RATE);
		rrm.setName(BizMetricDefine.AVAILAVLE_RATE_NAME);
		rrm.setUnit("%");
		rrm.setStyle(MetricTypeEnum.PerformanceMetric);
		rList.add(rrm);
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.MTTR);
		rrm.setName(BizMetricDefine.MTTR_NAME);
		rrm.setUnit("");
		rrm.setStyle(MetricTypeEnum.InformationMetric);
		rList.add(rrm);
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.MTBF);
		rrm.setName(BizMetricDefine.MTBF_NAME);
		rrm.setUnit("");
		rrm.setStyle(MetricTypeEnum.InformationMetric);
		rList.add(rrm);
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.DOWN_TIME);
		rrm.setName(BizMetricDefine.DOWN_TIME_NAME);
		rrm.setUnit("");
		rrm.setStyle(MetricTypeEnum.InformationMetric);
		rList.add(rrm);
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.OUTAGE_TIMES);
		rrm.setName(BizMetricDefine.OUTAGE_TIMES_NAME);
		rrm.setUnit("次");
		rrm.setStyle(MetricTypeEnum.InformationMetric);
		rList.add(rrm);

		//取消业务系统的响应速度
		/*rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.RESPONSE_TIME);
		rrm.setName(BizMetricDefine.RESPONSE_TIME_NAME);
		rrm.setUnit("ms");
		rrm.setStyle(MetricTypeEnum.PerformanceMetric);
		rList.add(rrm);*/
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.HOST_CAPACITY);
		rrm.setName(BizMetricDefine.HOST_CAPACITY_NAME);
		rrm.setUnit("%");
		rrm.setStyle(MetricTypeEnum.PerformanceMetric);
		rList.add(rrm);
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.STORAGE_CAPACITY);
		rrm.setName(BizMetricDefine.STORAGE_CAPACITY_NAME);
		rrm.setUnit("%");
		rrm.setStyle(MetricTypeEnum.PerformanceMetric);
		rList.add(rrm);
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.DATABASE_CAPACITY);
		rrm.setName(BizMetricDefine.DATABASE_CAPACITY_NAME);
		rrm.setUnit("%");
		rrm.setStyle(MetricTypeEnum.PerformanceMetric);
		rList.add(rrm);
		
		rrm = new ReportResourceMetric();
		rrm.setId(BizMetricDefine.BANDWIDTH_CAPACITY);
		rrm.setName(BizMetricDefine.BANDWIDTH_CAPACITY_NAME);
		rrm.setUnit("%");
		rrm.setStyle(MetricTypeEnum.PerformanceMetric);
		rList.add(rrm);
		
		return rList;
	}
	
	private Map<Long, List<Map<String, String>>> getMetricInfoByInsIdAndNodeId(long insId, long nodeId) throws InstancelibException {
		List<BizNodeMetricRelBo> BnmList = bizCanvasApi.getMetricByNodeId(nodeId);
		Map<Long ,List<String>> queryMap = new HashMap<Long ,List<String>>();
		if(null==BnmList||BnmList.size()==0){
			return null;
		}
		for(BizNodeMetricRelBo bnm:BnmList){
			Long ins = insId;
			if(bnm.getChildInstanceId()>0){
				ins = bnm.getChildInstanceId();
			}else{
				ins = insId;
			}
			if(queryMap.containsKey(ins)){
				queryMap.get(ins).add(bnm.getMetricId());
			}else{
				List<String> strList = new ArrayList<String>();
				strList.add(bnm.getMetricId());
				queryMap.put(ins, strList);
			}
		}
		ResourceInstance parent = resourceInstanceService.getResourceInstance(insId);
		if(parent.getLifeState()==InstanceLifeStateEnum.MONITORED){
			Map<Long, List<Map<String, String>>> bizMetricInfo = resourceApplyApi.getBizMetricInfo(queryMap, parent);
			return bizMetricInfo;
		}else{
			return null;//关联资源未监控!
		}
	}
}
