package com.mainsteam.stm.portal.business.web.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.bo.BizHealthHisBo;
import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizMainDataBo;
import com.mainsteam.stm.portal.business.dao.IBizHealthHisDao;
import com.mainsteam.stm.portal.business.service.util.BizMetricDefine;
import com.mainsteam.stm.portal.resource.bo.zTreeBo;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
/**
 * <li>文件名称: BizSelfAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2016年8月17日
 * @author   pengl
 */
@Controller
@RequestMapping("/portal/business/service")
public class BizMainAction extends BaseAction{
	
	@Autowired
	private BizMainApi bizMainApi;
	
	@Autowired
	private ILicenseCalcService licenseCalcService;
	
	@Resource
	private IUserApi stm_system_userApi;
	
	@Resource
	private IBizHealthHisDao bizHealthHisDao;
	
	@Value("${stm.module.refresh}")
	private String moduleRefresh;
	
	private Logger logger = Logger.getLogger(BizMainAction.class);
	
	/**
	 * 创建一个业务的基本信息
	 * @param session
	 * @param bizMain
	 * @return
	 */
	@RequestMapping("/insertBasicInfo")
	public JSONObject insertBasicInfo(HttpSession session,BizMainBo bizMain){
		
		if(!licenseCalcService.isLicenseEnough(LicenseModelEnum.stmMonitorBusi)){
			logger.error("portal.business.service.insert bussiness license over flow");
			return toFailForLicenseOverFlow("业务系统数超过license授权数量,无法继续添加!");
		}
		
		ILoginUser user = getLoginUser(session);
		bizMain.setCreateId(user.getId());
		bizMain.setCreateTime(new Date());
		
		long result = bizMainApi.insertBasicInfo(bizMain);
		
		if(result == -1){
			return toFailForGroupNameExsit(null);
		}else{
			return toSuccess(result);
		}
		
	}
	
	/**
	 * 修改一个业务的基本信息
	 * @param session
	 * @param bizMain
	 * @return
	 */
	@RequestMapping("/updateBasicInfo")
	public JSONObject updateBasicInfo(BizMainBo bizMain,String oldName){
		
		long result = bizMainApi.updateBasicInfo(bizMain,oldName);
		
		if(result == -1){
			return toFailForGroupNameExsit(null);
		}else{
			return toSuccess(result);
		}
		
	}
	
	/**
	 * 获取一个业务的基本信息
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/getBasicInfo")
	public JSONObject getBasicInfo(long bizId){
		return toSuccess(bizMainApi.getBasicInfo(bizId));
	}
	
	/**
	 * 获取一个业务的状态定义
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/getCanvasStatusDefine")
	public JSONObject getCanvasStatusDefine(long bizId){
		return toSuccess(bizMainApi.getCanvasStatusDefine(bizId));
	}
	
	/**
	 * 获取自动构建的资源列表
	 * @param session
	 * @param ip
	 * @return
	 */
	@RequestMapping("/getInstancesByAutoBuild")
	public JSONObject getInstancesByAutoBuild(HttpSession session,String ip){
		ILoginUser user = getLoginUser(session);
		return toSuccess(bizMainApi.getInstancesByAutoBuild(user, ip));
	}
	
	/**
	 * 获取手动构建的资源列表
	 * @param session
	 * @param ip
	 * @return
	 */
	@RequestMapping("/getInstancesByManualBuild")
	public JSONObject getInstancesByManualBuild(HttpSession session,String searchContent){
		ILoginUser user = getLoginUser(session);
		return toSuccess(bizMainApi.getInstancesByManualBuild(user, searchContent));
	}
	
	/**
	 * 获取手动构建的业务列表
	 * @param session
	 * @param ip
	 * @return
	 */
	@RequestMapping("/getBussinessByManualBuild")
	public JSONObject getBussinessByManualBuild(HttpSession session,long bizId,String searchContent){
		ILoginUser user = getLoginUser(session);
		return toSuccess(bizMainApi.getBussinessByManualBuild(user, bizId,searchContent));
	}
	
	/**
	 * 将选中资源进行自动构建
	 * @param bizId
	 * @param instanceIds
	 * @return
	 */
	@RequestMapping("/autoBuildBussiness")
	public JSONObject autoBuildBussiness(long bizId, String instanceIds){
		List<Long> ids = new ArrayList<Long>();
		for(String id : instanceIds.split(",")){
			ids.add(Long.parseLong(id));
		}
		Long[] longIds = new Long[ids.size()];
		return toSuccess(bizMainApi.autoBuildBussiness(bizId, ids.toArray(longIds)));
	}
	
	/**
	 * 查询指定业务的状态定义可加入参数
	 * @param bizId
	 * @param instanceIds
	 * @return
	 */
	@RequestMapping("/getBizStatusDefineParameter")
	public JSONObject getBizStatusDefineParameter(long bizId){
		return toSuccess(bizMainApi.getBizStatusDefineParameter(bizId));
	}
	
	/**
	 * 获取分页业务集合(汇总界面)
	 * @return
	 */
	@RequestMapping("/getPageListForSummary")
	public JSONObject getPageListForSummary(HttpSession session,int startNum,int pageSize){
		return toSuccess(bizMainApi.getPageListForSummary(getLoginUser(session),startNum,pageSize));
	}
	
	/**
	 * 获取业务top5响应速度(汇总界面)
	 * @return
	 */
	@RequestMapping("/getTopFiveResponseTime")
	public JSONObject getTopFiveResponseTime(HttpSession session){
		return toSuccess(bizMainApi.getTopFiveResponseTime(getLoginUser(session)));
	}
	
	/**
	 * 获取业务告警数量(汇总界面)
	 * @return
	 */
	@RequestMapping("/getBizAlarmCount")
	public JSONObject getBizAlarmCount(HttpSession session,String startTime,String endTime){
	    if (startTime == null || endTime == null){
	        long nowTime = System.currentTimeMillis();
	        endTime = String.valueOf(nowTime);
	        startTime = String.valueOf(nowTime - (7 * 24 * 60 * 60 * 1000));
        }
		Date startDate = new Date(Long.parseLong(startTime));
		Date endDate = new Date(Long.parseLong(endTime));
		return toSuccess(bizMainApi.getBizAlarmCount(getLoginUser(session),startDate,endDate));
	}
	
	/**
	 * 获取业务运行情况(汇总界面)
	 * @return
	 */
	@RequestMapping("/getPageListRunInfo")
	public JSONObject getPageListRunInfo(HttpSession session,String startTime,String endTime){
		Date startDate = new Date(Long.parseLong(startTime));
		Date endDate = new Date(Long.parseLong(endTime));
		return toSuccess(bizMainApi.getPageListRunInfo(getLoginUser(session),startDate,endDate));
	}
	
	/**
	 * 获取业务计算容量(汇总界面)
	 * @return
	 */
	@RequestMapping("/getCalculateCapacityInfo")
	public JSONObject getCalculateCapacityInfo(HttpSession session,int startNum,int pageSize){
		return toSuccess(bizMainApi.getCalculateCapacityInfo(getLoginUser(session),startNum,pageSize));
	}
	
	/**
	 * 获取业务存储容量(汇总界面)
	 * @return
	 */
	@RequestMapping("/getStoreCapacityInfo")
	public JSONObject getStoreCapacityInfo(HttpSession session,int startNum,int pageSize){
		return toSuccess(bizMainApi.getStoreCapacityInfo(getLoginUser(session),startNum,pageSize));
	}
	
	/**
	 * 获取业务数据库容量(汇总界面)
	 * @return
	 */
	@RequestMapping("/getDatabaseCapacityInfo")
	public JSONObject getDatabaseCapacityInfo(HttpSession session,int startNum,int pageSize){
		return toSuccess(bizMainApi.getDatabaseCapacityInfo(getLoginUser(session),startNum,pageSize));
	}
	
	/**
	 * 获取分页业务集合(列表界面)
	 * @param user 登录用户
	 * @param status 查询状态
	 * @param startTime 指标统计开始时间
	 * @param endTime 指标统计结束时间
	 * @param queryName 查询业务名称
	 * @param startNum 分页开始行数
	 * @param pageSize 分页每页行数
	 * @return
	 */
	@RequestMapping("/getPageListForGrid")
	public JSONObject getPageListForGrid(HttpSession session,int status,Date startTime,Date endTime,
			String queryName, int startNum,int pageSize){
		return toSuccess(bizMainApi.getPageListForGrid(getLoginUser(session), status, startTime, endTime,
				 queryName,  startNum, pageSize));
	}
	
	@RequestMapping("/getListPages")
	public JSONObject getListPages(Page<BizMainDataBo, Object> page, HttpSession session,HttpServletRequest request,int status,
			String queryName) throws ParseException{
//		queryName="第一个业务系统";
	Date endTime=new Date();
	Date startTime=new Date(endTime.getTime() - 7 * 24 * 60 * 60 * 1000);
		return toSuccess(bizMainApi.getPageListForGridOrder(getLoginUser(session), status,startTime, endTime,
				 queryName,  (int)page.getStartRow(), (int)page.getRowCount(),page.getOrder(),page.getSort()));
		
		
	}
	
	/**
	 * 获取业务数据库容量(汇总界面)
	 * @return
	 */
	@RequestMapping("/deleteBiz")
	public JSONObject deleteBiz(String ids){
		if(ids == null || ids.equals("")){
			return toSuccess(false);
		}
		String[] idArray = ids.split(",");
		long[] idList = new long[idArray.length];
		for(int i = 0 ; i < idArray.length ; i ++){
			idList[i] = Long.parseLong(idArray[i]);
		}
		return toSuccess(bizMainApi.deleteBiz(idList));
	}
	
	/**
	 * 获取业务带宽容量(汇总界面)
	 * @return
	 */
	@RequestMapping("/getBandwidthCapacityInfo")
	public JSONObject getBandwidthCapacityInfo(HttpSession session,int startNum,int pageSize){
		return toSuccess(bizMainApi.getBandwidthCapacityInfo(getLoginUser(session),startNum,pageSize));
	}
	
	/**
	 * 获取业务列表基本信息
	 * @return
	 */
	@RequestMapping("/getAllBizList")
	public JSONObject getAllBizList(HttpSession session){
		return toSuccess(bizMainApi.getAllBizList(getLoginUser(session)));
	}
	
	/**
	 * 获取所有创建者及责任人信息
	 * @return
	 */
	@RequestMapping("/getAllPermissionsInfoList")
	public JSONObject getAllPermissionsInfoList(){
		return toSuccess(bizMainApi.getAllPermissionsInfoList());
	}
	
	/**
	 * 获取单个业务详情运行情况(绘图tooltip)
	 * @return
	 */
	@RequestMapping("/getRunInfoForTooltip")
	public JSONObject getRunInfoForTooltip(Long bizId){
		return toSuccess(bizMainApi.getRunInfoForTooltip(bizId));
	}
	
	/**
	 * 修改业务状态定义
	 * @return
	 */
	@RequestMapping("/updateBizStatusDefine")
	public JSONObject updateBizStatusDefine(BizMainBo mainBo){
		return toSuccess(bizMainApi.updateBizStatusDefine(mainBo));
	}
	
	/**
	 * 查询单个资源的容量指标信息
	 * @return
	 */
	@RequestMapping("/getCapacityMetric")
	public JSONObject getCapacityMetric(Long bizId){
		
		return toSuccess(bizMainApi.getCapacityMetric(bizId));
	}
	
	/**
	 * 业务详情获取运行状况
	 * @return
	 */
	@RequestMapping("/getRunInfoForDetail")
	public JSONObject getRunInfoForDetail(long bizId,String startTime,String endTime){
		Date startDate = new Date(Long.parseLong(startTime));
		Date endDate = new Date(Long.parseLong(endTime));
		return toSuccess(bizMainApi.getRunInfoForDetail(bizId, startDate, endDate));
	}
	
	/**
	 * 业务健康度度详情
	 * @return
	 */
	@RequestMapping("/getHealthDetail")
	public JSONObject getHealthDetail(long bizId){
		return toSuccess(bizMainApi.getHealthDetail(bizId));
	}
	
	@RequestMapping("/getAllViewBiz")
	public JSONObject getAllViewBiz(HttpSession session){
		return toSuccess(bizMainApi.getAllViewBiz(getLoginUser(session)));
	}
	
	@RequestMapping("/getBizListByInstanceId")
	public JSONObject getBizListByInstanceId(HttpSession session,Long instanceId){
		return toSuccess(bizMainApi.getBizListByInstanceId(getLoginUser(session),instanceId));
	}
	
	/**
	 * 业务健康度度详情和分数
	 * @return
	 */
	@RequestMapping("/getHealthDetailAndScore")
	public JSONObject getHealthDetailAndScore(long bizId){
		Map<String, Object> result = new HashMap<String, Object>();
		BizHealthHisBo health = bizHealthHisDao.getBizHealthHis(bizId);
		if(health == null){
			result.put("health", 100);
			result.put("status", 0);
		}else{
			result.put("health", health.getBizHealth());
			result.put("status", health.getBizStatus());
		}
		result.put("detail", bizMainApi.getHealthDetail(bizId));
		return toSuccess(result);
	}
	
	/**
	 * 业务健康度度历史详情
	 * @return
	 */
	@RequestMapping("/getHealthHistoryData")
	public JSONObject getHealthHistoryData(long bizId,String startTime,String endTime,String timeType){
		Date startDate = new Date(Long.parseLong(startTime));
		Date endDate = new Date(Long.parseLong(endTime));
		return toSuccess(bizMainApi.getHealthHistoryData(bizId, startDate, endDate,timeType));
	}
	
	/**
	 * 业务响应速度详情
	 * @return
	 */
	@RequestMapping("/getResponseTimeHistoryData")
	public JSONObject getResponseTimeHistoryData(long bizId,String startTime,String endTime,String timeType){
		Date startDate = new Date(Long.parseLong(startTime));
		Date endDate = new Date(Long.parseLong(endTime));
		return toSuccess(bizMainApi.getResponseTimeHistoryData(bizId,startDate,endDate,timeType));
	}
	
	/**
	 * 查询所有的用户(不包含超级管理员)
	 * @param userPage
	 * @return
	 */
	@RequestMapping("/getManagerUsers")
	public JSONObject getManagerUsers(String searchContent){
		List<User> datas = stm_system_userApi.queryAllUserNoPage();
		List<User> result = new ArrayList<User>();
		if(null != datas && datas.size() > 0){
			for(User u : datas){
				if(u.getId() != 1l && u.getStatus() == 1){
					if(searchContent != null && !searchContent.equals("")){
						if(u.getAccount().toUpperCase().contains(searchContent.toUpperCase())){
							result.add(u);
						}
					}else{
						result.add(u);
					}
				}
			}
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("total", result.size());
		map.put("rows", result);
		
		return toSuccess(map);
	}

	@RequestMapping("/getMuduleTime")
	public JSONObject getMuduleTime(){
		return toSuccess(moduleRefresh);
	}
	
	/**
	 * 为首页提供业务指标数据
	 * @return
	 */
	@RequestMapping("/getBizMetricInfo")
	public JSONObject getBizMetricInfo(){
		
		JSONArray result = new JSONArray();
		
		JSONObject metric = new JSONObject();
		
		metric.put("id", BizMetricDefine.AVAILAVLE_RATE);
		metric.put("name", BizMetricDefine.AVAILAVLE_RATE_NAME);
		metric.put("unit", "%");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.RESPONSE_TIME);
		metric.put("name", BizMetricDefine.RESPONSE_TIME_NAME);
		metric.put("unit", "ms");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.BIZ_HEALTH_STATUS);
		metric.put("name", BizMetricDefine.BIZ_HEALTH_STATUS_NAME);
		metric.put("unit", "分");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.MTTR);
		metric.put("name", BizMetricDefine.MTTR_NAME);
		metric.put("unit", "小时");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.MTBF);
		metric.put("name", BizMetricDefine.MTBF_NAME);
		metric.put("unit", "天");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.DOWN_TIME);
		metric.put("name", BizMetricDefine.DOWN_TIME_NAME);
		metric.put("unit", "小时");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.OUTAGE_TIMES);
		metric.put("name", BizMetricDefine.OUTAGE_TIMES_NAME);
		metric.put("unit", "次");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.HOST_CAPACITY);
		metric.put("name", BizMetricDefine.HOST_CAPACITY_NAME);
		metric.put("unit", "%");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.DATABASE_CAPACITY);
		metric.put("name", BizMetricDefine.DATABASE_CAPACITY_NAME);
		metric.put("unit", "%");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.STORAGE_CAPACITY);
		metric.put("name", BizMetricDefine.STORAGE_CAPACITY_NAME);
		metric.put("unit", "%");
		result.add(metric);
		
		metric = new JSONObject();
		metric.put("id", BizMetricDefine.BANDWIDTH_CAPACITY);
		metric.put("name", BizMetricDefine.BANDWIDTH_CAPACITY_NAME);
		metric.put("unit", "%");
		result.add(metric);
		
		return toSuccess(result);
		
	}
	
	
	/**
	 * 为首页提供业务topn排序
	 * @return
	 */
	@RequestMapping("/getBizTopnInfo")
	public JSONObject getBizTopnInfo(HttpSession session,String resource,String metric,Integer top,String sortMethod){
		return toSuccess(bizMainApi.getMetricTopForHome(getLoginUser(session), resource, metric, top, sortMethod));
	}
	
	/**
	 * 为首页提供业务关注指标数据
	 * @return
	 */
	@RequestMapping("/getBizFocusMetric")
	public JSONObject getBizFocusMetric(HttpSession session,String searchContent){
		
		zTreeBo tree = new zTreeBo();
		tree.setId("business");
		tree.setName("业务系统");
		tree.setChecked(false);
		tree.setNocheck(false);
		tree.setOpen(false);
		tree.setIsParent(true);
		
		List<zTreeBo> bizZtree = new ArrayList<zTreeBo>();
		
		List<BizMainBo> bizList = bizMainApi.getAllViewBiz(getLoginUser(session));
		
		if(bizList != null && bizList.size() > 0){
			
			Map<String, String> hisAttribute = new HashMap<String, String>();
			hisAttribute.put("history", "true");
			hisAttribute.put("point", "true");
			Map<String, String> pointAttribute = new HashMap<String, String>();
			pointAttribute.put("history", "false");
			pointAttribute.put("point", "true");
			
			for(BizMainBo biz : bizList){
				
				if(searchContent != null && !searchContent.equals("")){
					if(!(biz.getName().toUpperCase().contains(searchContent.toUpperCase()))){
						continue;
					}
				}
				
				zTreeBo bizTree = new zTreeBo();
				bizTree.setId(biz.getId() + "");
				bizTree.setChecked(false);
				bizTree.setIsParent(true);
				bizTree.setName(biz.getName());
				bizTree.setNocheck(false);
				bizTree.setOpen(false);
				bizTree.setPId("business");
				
				List<zTreeBo> metricZtree = new ArrayList<zTreeBo>();
				
				zTreeBo metricTreeHealth = new zTreeBo();
				metricTreeHealth.setChecked(false);
				metricTreeHealth.setChildren(null);
				metricTreeHealth.setExtendAttribute(hisAttribute);
				metricTreeHealth.setId(BizMetricDefine.BIZ_HEALTH_STATUS);
				metricTreeHealth.setIsParent(false);
				metricTreeHealth.setName(BizMetricDefine.BIZ_HEALTH_STATUS_NAME);
				metricTreeHealth.setNocheck(false);
				metricTreeHealth.setOpen(false);
				metricTreeHealth.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealth);
				
				zTreeBo metricTreeHealthAvailavle = new zTreeBo();
				metricTreeHealthAvailavle.setChecked(false);
				metricTreeHealthAvailavle.setChildren(null);
				metricTreeHealthAvailavle.setExtendAttribute(pointAttribute);
				metricTreeHealthAvailavle.setId(BizMetricDefine.AVAILAVLE_RATE);
				metricTreeHealthAvailavle.setIsParent(false);
				metricTreeHealthAvailavle.setName(BizMetricDefine.AVAILAVLE_RATE_NAME);
				metricTreeHealthAvailavle.setNocheck(false);
				metricTreeHealthAvailavle.setOpen(false);
				metricTreeHealthAvailavle.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthAvailavle);
				
				zTreeBo metricTreeHealthMttr = new zTreeBo();
				metricTreeHealthMttr.setChecked(false);
				metricTreeHealthMttr.setChildren(null);
				metricTreeHealthMttr.setExtendAttribute(pointAttribute);
				metricTreeHealthMttr.setId(BizMetricDefine.MTTR);
				metricTreeHealthMttr.setIsParent(false);
				metricTreeHealthMttr.setName(BizMetricDefine.MTTR_NAME);
				metricTreeHealthMttr.setNocheck(false);
				metricTreeHealthMttr.setOpen(false);
				metricTreeHealthMttr.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthMttr);
				
				zTreeBo metricTreeHealthMtbf = new zTreeBo();
				metricTreeHealthMtbf.setChecked(false);
				metricTreeHealthMtbf.setChildren(null);
				metricTreeHealthMtbf.setExtendAttribute(pointAttribute);
				metricTreeHealthMtbf.setId(BizMetricDefine.MTBF);
				metricTreeHealthMtbf.setIsParent(false);
				metricTreeHealthMtbf.setName(BizMetricDefine.MTBF_NAME);
				metricTreeHealthMtbf.setNocheck(false);
				metricTreeHealthMtbf.setOpen(false);
				metricTreeHealthMtbf.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthMtbf);
				
				zTreeBo metricTreeHealthDown = new zTreeBo();
				metricTreeHealthDown.setChecked(false);
				metricTreeHealthDown.setChildren(null);
				metricTreeHealthDown.setExtendAttribute(pointAttribute);
				metricTreeHealthDown.setId(BizMetricDefine.DOWN_TIME);
				metricTreeHealthDown.setIsParent(false);
				metricTreeHealthDown.setName(BizMetricDefine.DOWN_TIME_NAME);
				metricTreeHealthDown.setNocheck(false);
				metricTreeHealthDown.setOpen(false);
				metricTreeHealthDown.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthDown);
				
				zTreeBo metricTreeHealthOutage = new zTreeBo();
				metricTreeHealthOutage.setChecked(false);
				metricTreeHealthOutage.setChildren(null);
				metricTreeHealthOutage.setExtendAttribute(pointAttribute);
				metricTreeHealthOutage.setId(BizMetricDefine.OUTAGE_TIMES);
				metricTreeHealthOutage.setIsParent(false);
				metricTreeHealthOutage.setName(BizMetricDefine.OUTAGE_TIMES_NAME);
				metricTreeHealthOutage.setNocheck(false);
				metricTreeHealthOutage.setOpen(false);
				metricTreeHealthOutage.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthOutage);
				
				zTreeBo metricTreeHealthResponse = new zTreeBo();
				metricTreeHealthResponse.setChecked(false);
				metricTreeHealthResponse.setChildren(null);
				metricTreeHealthResponse.setExtendAttribute(hisAttribute);
				metricTreeHealthResponse.setId(BizMetricDefine.RESPONSE_TIME);
				metricTreeHealthResponse.setIsParent(false);
				metricTreeHealthResponse.setName(BizMetricDefine.RESPONSE_TIME_NAME);
				metricTreeHealthResponse.setNocheck(false);
				metricTreeHealthResponse.setOpen(false);
				metricTreeHealthResponse.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthResponse);
				
				zTreeBo metricTreeHealthHost = new zTreeBo();
				metricTreeHealthHost.setChecked(false);
				metricTreeHealthHost.setChildren(null);
				metricTreeHealthHost.setExtendAttribute(pointAttribute);
				metricTreeHealthHost.setId(BizMetricDefine.HOST_CAPACITY);
				metricTreeHealthHost.setIsParent(false);
				metricTreeHealthHost.setName(BizMetricDefine.HOST_CAPACITY_NAME);
				metricTreeHealthHost.setNocheck(false);
				metricTreeHealthHost.setOpen(false);
				metricTreeHealthHost.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthHost);
				
				zTreeBo metricTreeHealthStorage = new zTreeBo();
				metricTreeHealthStorage.setChecked(false);
				metricTreeHealthStorage.setChildren(null);
				metricTreeHealthStorage.setExtendAttribute(pointAttribute);
				metricTreeHealthStorage.setId(BizMetricDefine.STORAGE_CAPACITY);
				metricTreeHealthStorage.setIsParent(false);
				metricTreeHealthStorage.setName(BizMetricDefine.STORAGE_CAPACITY_NAME);
				metricTreeHealthStorage.setNocheck(false);
				metricTreeHealthStorage.setOpen(false);
				metricTreeHealthStorage.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthStorage);
				
				zTreeBo metricTreeHealthDatabase = new zTreeBo();
				metricTreeHealthDatabase.setChecked(false);
				metricTreeHealthDatabase.setChildren(null);
				metricTreeHealthDatabase.setExtendAttribute(pointAttribute);
				metricTreeHealthDatabase.setId(BizMetricDefine.DATABASE_CAPACITY);
				metricTreeHealthDatabase.setIsParent(false);
				metricTreeHealthDatabase.setName(BizMetricDefine.DATABASE_CAPACITY_NAME);
				metricTreeHealthDatabase.setNocheck(false);
				metricTreeHealthDatabase.setOpen(false);
				metricTreeHealthDatabase.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthDatabase);
				
				zTreeBo metricTreeHealthBandwidth = new zTreeBo();
				metricTreeHealthBandwidth.setChecked(false);
				metricTreeHealthBandwidth.setChildren(null);
				metricTreeHealthBandwidth.setExtendAttribute(pointAttribute);
				metricTreeHealthBandwidth.setId(BizMetricDefine.BANDWIDTH_CAPACITY);
				metricTreeHealthBandwidth.setIsParent(false);
				metricTreeHealthBandwidth.setName(BizMetricDefine.BANDWIDTH_CAPACITY_NAME);
				metricTreeHealthBandwidth.setNocheck(false);
				metricTreeHealthBandwidth.setOpen(false);
				metricTreeHealthBandwidth.setPId(biz.getId() + "");
				metricZtree.add(metricTreeHealthBandwidth);
				
				bizTree.setChildren(metricZtree);
				
				bizZtree.add(bizTree);
			}
			
			if(bizZtree.size() <= 0){
				return toSuccess(null);
			}
			
		}
		
		tree.setChildren(bizZtree);
		
		return toSuccess(tree);
	}
	
	/**
	 * 为首页提供业务关注指标数据
	 * @return
	 */
	@RequestMapping("/getBizFocusHistoryMetricData")
	public JSONObject getBizFocusHistoryMetricData(String query){
		
		JSONObject output = new JSONObject(); 
		JSONArray qy = (JSONArray)JSONArray.parse(query);
		
		for(int i = 0; i < qy.size(); i++){
			JSONObject mdq  = qy.getJSONObject(i);
			
			String summaryType = mdq.getString("summaryType");
			long instanceId = mdq.getLongValue("instanceId");
			String[] metricId = mdq.getString("metricId").split(",");
			long dateStart = mdq.getLongValue("dateStart");
			long dateEnd =mdq.getLongValue("dateEnd");
			
			MetricSummaryType mtyp = null;
			try{
				if(summaryType.equals("hour")){
					summaryType = "H";
				}
				mtyp = MetricSummaryType.valueOf(summaryType);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			Map<String, Object> dat = new HashMap<String, Object>();
			for(String metric : metricId){
				
				if(metric.equals(BizMetricDefine.BIZ_HEALTH_STATUS)){
					dat.put(metric, bizMainApi.getHealthHistoryDataForHomeMetric(instanceId, new Date(dateStart), new Date(dateEnd)));
				}else if(metric.equals(BizMetricDefine.RESPONSE_TIME)){
					dat.put(metric, bizMainApi.getResponseTimeHistoryDataForHomeMetric(instanceId, new Date(dateStart), new Date(dateEnd), mtyp));
				}
				
			}
			output.put(instanceId + "", dat);
		}
		
		return  toSuccess(output);
	}
	
	/**
	 * 查询多个指标的当前值
	 * @param query
	 * @return
	 */
	@RequestMapping("/getBizFocusRealMetricData")
	public JSONObject getBizFocusRealMetricData(String query){
		JSONObject output = new JSONObject(); 
		JSONObject instances = (JSONObject)JSONObject.parse(query);
		
		//JSONObject instances = qy.getJSONObject("instance");
		for(String iid: instances.keySet()){
			
			JSONObject instance = instances.getJSONObject(iid);
			String[] metrics = instance.getString("metric").split(",");
			JSONObject metricData = new JSONObject();
			for (String mt : metrics) {
				
				if(mt.equals("")){
					continue;
				}
				Map<String, Object> tdata = bizMainApi.getFocusMetricDataForHome(Long.parseLong(iid), mt);
				
				metricData.put(mt,tdata);
				
			}
			
			output.put(iid, metricData);
			
		}
		
		return toSuccess(output);
	}
	
}
