/**
 * 
 */
package com.mainsteam.stm.portal.resourcelog.web.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.resourcelog.snmptrap.api.ISnmptrapStrategyApi;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;
import com.mainsteam.stm.system.resource.api.Filter;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.util.IConstant;
import com.mainsteam.stm.util.Util;

/**
 * <li>文件名称: SnmpTrapAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月12日
 * @author   lil
 */
@Controller
@RequestMapping("resourcelog/snmptrap")
public class SnmpTrapAction extends BaseAction {
	
	private static final Logger LOGGER = Logger.getLogger(SnmpTrapAction.class);
	
	@Resource
	private ISnmptrapStrategyApi snmptrapStrategyApi;
	
	@Autowired
	private IResourceApi resourceApi;
	/**
	 * 
	* @Title: queryStrategy
	* @Description: 查询策略信息
	* @param page
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("queryStrategy")
	public JSONObject queryStrategy(Page<StrategyBo, StrategyBo> page,StrategyBo sBo) {
		ILoginUser loginUser = getLoginUser();
		Set<IDomain> domains = loginUser.getDomains();
		List<Long> domainList = new ArrayList<Long>();
		
		Iterator<IDomain> iter = domains.iterator(); 
		while (iter.hasNext()) {
			domainList.add(iter.next().getId());
		}
		sBo.setDomainIds(domainList);
		page.setCondition(sBo);
		//根据创建者ID条件查询策略
		this.snmptrapStrategyApi.queryStrategy(page);
		return toSuccess(page);
	}
	
	/**
	 * 
	* @Title: querySetStrategy
	* @Description: 查询要可以更改的策略
	* @param page
	* @param sBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("querySetStrategy")
	public JSONObject querySetStrategy(Page<StrategyBo, StrategyBo> page,StrategyBo sBo) {
		page.setCondition(sBo);
		//根据创建者ID条件查询策略
		this.snmptrapStrategyApi.queryStrategy(page);
		return toSuccess(page);
	}
	/**
	 * 
	* @Title: removeStrategy
	* @Description: 批量删除策略信息
	* @param delIds
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("removeStrategy")
	public JSONObject removeStrategy(Long[] delIds) {
		int returnDelId = this.snmptrapStrategyApi.delStrategy(delIds);
		
		return toSuccess(returnDelId > 0 ? true : false);
	}
	
	@RequestMapping("findStrategyResource")
	public JSONObject findStrategyResource(SyslogResourceBo syslogBo) {
		String[] categoryIdArr = syslogBo.getCategoryId().split(",");
		List<String> categoryIds = Arrays.asList(categoryIdArr);
		
		final String searchKey = syslogBo.getName();
		//根据条件查询所有资源信息
		ResourceQueryBo queryBo = new ResourceQueryBo(getLoginUser());
		queryBo.setDomainIds(syslogBo.getDomainIdsList());
		queryBo.setCategoryIds(categoryIds);
		queryBo.setFilter(new Filter() {
			@Override
			public boolean filter(ResourceInstanceBo riBo) {
				if (!"".equals(searchKey) && searchKey != null) {
					if (riBo.getDiscoverIP().contains(searchKey) || riBo.getShowName().contains(searchKey)) {
						return true;
					}else {
						return false;
					}
				}
				return true;
			}
		});
		
		//得到资源类型的映射
		Map<String, String> categoryMap = this.resourceApi.getCategoryMapper();
		List<ResourceInstanceBo> listAll = this.resourceApi.getResources(queryBo);
		//判断资源是否已挂在策略下，如果是，则移除该资源
		List<SyslogResourceBo> listSyslogRes = new ArrayList<SyslogResourceBo>();
		int countRes = 0;
		SyslogResourceBo syslogRes;
		for (ResourceInstanceBo resourceInstanceBo : listAll) {
			syslogRes = new SyslogResourceBo();
			syslogRes.setResourceId(resourceInstanceBo.getId());
			syslogRes.setName(resourceInstanceBo.getShowName());
			syslogRes.setResourceType(categoryMap.get(resourceInstanceBo.getCategoryId()));
			syslogRes.setResourceIp(resourceInstanceBo.getDiscoverIP());
			syslogRes.setStrategyType(syslogBo.getStrategyType());
			countRes = this.snmptrapStrategyApi.countRes(syslogRes);
			//资源没有挂在策略上才添加
			if (countRes <= 0) {
				listSyslogRes.add(syslogRes);
			}
		}
		return toSuccess(listSyslogRes);
	}
	
	/**
	 * 
	* @Title: getResourceByStrategy
	* @Description: 根据策略ID获取资源实例
	* @param strategyId
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getResourceByStrategy")
	public JSONObject getResourceByStrategy(Long strategyId) {
		//得到资源类型的映射
		Map<String, String> categoryMap = this.resourceApi.getCategoryMapper();
		List<SyslogResourceBo> list = this.snmptrapStrategyApi.getResource(strategyId);
		for (SyslogResourceBo syslogResourceBo : list) {
			if (0 != syslogResourceBo.getResourceId()) {
				ResourceInstanceBo res =  resourceApi.getResource(syslogResourceBo.getResourceId());
				if (res.getDiscoverIP() != null && res.getName() != null) {
					syslogResourceBo.setName(res.getName());
					syslogResourceBo.setResourceIp(res.getDiscoverIP());
					syslogResourceBo.setResourceType(categoryMap.get(res.getCategoryId()));
				}
			}
		}
		return toSuccess(list);
	}
	
	/**
	 * 
	* @Title: getBasicStrategy
	* @Description: 根据ID获取基本策略信息
	* @param strategyId
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getBasicStrategy")
	public JSONObject getBasicStrategy(Long id) {
		StrategyBo strategyBo = this.snmptrapStrategyApi.getStrategyBasic(id);
		return toSuccess(strategyBo);
	}
	
	/**
	 * 
	* @Title: saveSyslogStrategyBasic
	* @Description: 保存策略基本信息
	* @param strategy
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveSyslogStrategyBasic")
	public JSONObject saveSyslogStrategyBasic(StrategyBo strategy) {
		boolean isNameExist = this.snmptrapStrategyApi.isStrategyNameExist(strategy);
		if (!isNameExist) {
			int ret = this.snmptrapStrategyApi.saveStrategyBasic(strategy);
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("successRow", ret);
			retMap.put("strategyId", strategy.getId());
			return toSuccess(retMap);
		} else {
			return toFailForGroupNameExsit("保存失败!策略名在所选域中已存在!");
		}
	}
	
	/**
	 * 
	* @Title: updateSyslogStrategyBasic
	* @Description: 更新基本策略信息
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateSyslogStrategyBasic")
	public JSONObject updateSyslogStrategyBasic(StrategyBo strategy) {
		boolean isNameExist = this.snmptrapStrategyApi.isStrategyNameExist(strategy);
		if (!isNameExist) {
			int ret = this.snmptrapStrategyApi.updateStrategyBasic(strategy);
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("successRow", ret);
			retMap.put("strategyId", strategy.getId());
			return toSuccess(retMap);
		} else {
			return toFailForGroupNameExsit("修改失败!策略名在所选域中已存在!");
		}
	}
	
	/**
	 * 
	* @Title: saveStrategyResource
	* @Description: 保存策略资源
	* @param strategyId
	* @param rightDatas
	* @param strategyType
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveStrategyResource")
	public JSONObject saveStrategyResource(Long strategyId, String rightDatas,int strategyType) {
		List<SyslogResourceBo> listRes = JSONObject.parseArray(rightDatas, SyslogResourceBo.class);
		int ret = this.snmptrapStrategyApi.saveStrategyResource(strategyId, listRes,strategyType);
		return toSuccess(ret);
	}
	

	/**
	 * 
	* @Title: updateMonitorState
	* @Description: 更新监听状态
	* @param syslogResourceBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateMonitorState")
	public JSONObject updateMonitorState(Long[] id, int isMonitor) {
		return toSuccess(this.snmptrapStrategyApi.updateMonitorState(id,isMonitor));
	}
	
	/**
	 * 
	* @Title: querySyslogList
	* @Description: 查询策略日志
	* @param page
	* @param strategyBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("querySyslogList")
	public JSONObject querySyslogList(Page<SyslogResourceBo, StrategyBo> page,StrategyBo strategyBo) {
		List<Long> domainList = new ArrayList<Long>();
		if (null == strategyBo.getDomainId() || 0 == strategyBo.getDomainId()) {
			ILoginUser loginUser = getLoginUser();
			Set<IDomain> domains = loginUser.getDomains();
			
			Iterator<IDomain> iter = domains.iterator(); 
			while (iter.hasNext()) {
				domainList.add(iter.next().getId());
			}
		} else {
			domainList.add(strategyBo.getDomainId());
		}
		strategyBo.setDomainIds(domainList);
		strategyBo.setId((long) 0);
		page.setCondition(strategyBo);
		//得到资源类型的映射
		Map<String, String> categoryMap = this.resourceApi.getCategoryMapper();
		//查询资源
		List<SyslogResourceBo> listLog = this.snmptrapStrategyApi.getTrapLog(page);
		for (SyslogResourceBo syslogResourceBo : listLog) {
			if (0 != syslogResourceBo.getResourceId()) {
				ResourceInstanceBo res =  resourceApi.getResource(syslogResourceBo.getResourceId());
				syslogResourceBo.setName(res.getShowName());
				syslogResourceBo.setResourceIp(res.getDiscoverIP());
				syslogResourceBo.setResourceType(categoryMap.get(res.getCategoryId()));
			}else {
				syslogResourceBo.setName(syslogResourceBo.getResourceIp());
				syslogResourceBo.setResourceIp(syslogResourceBo.getResourceIp());
			}
		}
		
		Iterator<SyslogResourceBo> it = listLog.iterator();
		while(it.hasNext()){
			SyslogResourceBo syslogResourceBos = it.next();
			if(Util.isEmpty(syslogResourceBos.getName())){
				it.remove();
			}
		}
		
		page.setDatas(listLog);
		return toSuccess(page);
	}
	
	/**
	 * 
	* @Title: updateResourceStrategy
	* @Description: 更新资源下的策略信息
	* @param syslogResourceBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateResourceStrategy")
	public JSONObject updateResourceStrategy(SyslogResourceBo syslogResourceBo) {
		return toSuccess(this.snmptrapStrategyApi.updateResourceStrategy(syslogResourceBo));
	}
	/**
	 * 
	* @Title: saveSnmpTrapRule
	* @Description: 保存snmptrap规则
	* @param strategyBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveSnmpTrapRule")
	public JSONObject saveSnmpTrapRule(StrategyBo strategyBo) {
		return toSuccess(this.snmptrapStrategyApi.updateSnmptrapType(strategyBo));
	}
	
	/**
	 * 
	* @Title: getSnmptrapHistory
	* @Description: 查询当天历史记录
	* @param page
	* @param alarmListBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getSnmptrapHistory")
	public JSONObject getSnmptrapHistory(Page<AlarmListBo, AlarmListBo> page,AlarmListBo alarmListBo) {
		if ("-1".equals(alarmListBo.getSnmptrapType())) {
			alarmListBo.setSnmptrapType("");
		}
		alarmListBo.setOccurredTime(new Date());
		alarmListBo.setStartTime("");
		alarmListBo.setEndTime("");
		page.setCondition(alarmListBo);
		List<AlarmListBo> listAlarm = this.snmptrapStrategyApi.getSnmptrapHistory(page);
		page.setDatas(listAlarm);
		return toSuccess(page);
	}
	
	/**
	 * 
	* @Title: getTotalAlarm
	* @Description: 查询所有的告警列表
	* @param page
	* @param alarmListBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getTotalSnmptrap")
	public JSONObject getTotalSnmptrap(Page<AlarmListBo, AlarmListBo> page,AlarmListBo alarmListBo) {
		if ("-1".equals(alarmListBo.getSnmptrapType())) {
			alarmListBo.setSnmptrapType("");
		}
		page.setCondition(alarmListBo);
		List<AlarmListBo> listAlarm = this.snmptrapStrategyApi.getSnmptrapHistory(page);
		page.setDatas(listAlarm);
		return toSuccess(page);
	}
	
	/**
	 * 
	* @Title: saveIpAddress
	* @Description: 保存IP地址
	* @param syslogResBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveIpAddress")
	public JSONObject saveIpAddress(final SyslogResourceBo syslogResBo){
		int saveCount = 0;
		//判断输入的IP是否存在资源中
		ResourceQueryBo queryBo = new ResourceQueryBo(getLoginUser());
		queryBo.setFilter(new Filter() {
			@Override
			public boolean filter(ResourceInstanceBo riBo) {
				//排除IP为空字符
				if (riBo.getDiscoverIP() != null && !riBo.getDiscoverIP().isEmpty()) {
					//判断IP是否在数组中
					if (Arrays.binarySearch(syslogResBo.getSnmptrapIp().split(","), riBo.getDiscoverIP()) < 0) {
						return false;
					}
					return true;
				}
				return false;
			}
		});
		List<ResourceInstanceBo> listRes = null;
		try {
			listRes = resourceApi.getResources(queryBo);
		} catch (Exception e) {
			LOGGER.debug("查询资源失败!", e);
		}
		List<SyslogResourceBo> listSyslog = new ArrayList<SyslogResourceBo>();
		if (listRes != null && !listRes.isEmpty()) {
			for (ResourceInstanceBo resourceInstanceBo : listRes) {
				SyslogResourceBo resourceBo = new SyslogResourceBo();
				resourceBo.setResourceId(resourceInstanceBo.getId());
				resourceBo.setResourceIp(resourceInstanceBo.getDiscoverIP());
				resourceBo.setStrategyId(syslogResBo.getStrategyId());
				resourceBo.setStrategyType(syslogResBo.getStrategyType());
				listSyslog.add(resourceBo);
			}
			return toSuccess(this.snmptrapStrategyApi.saveIpResource(listSyslog));
//			return toSuccess(this.snmptrapStrategyApi.saveStrategyResource(syslogResBo.getStrategyId(), listSyslog,syslogResBo.getStrategyType()));
		} else {
			String[] ipArr = syslogResBo.getSnmptrapIp().split(IConstant.p_comma_);
			for (int i = 0; i < ipArr.length; i++) {
				syslogResBo.setSnmptrapIp(ipArr[i]);
				//判断IP是否存在,返回true存在,false不存在
				boolean isIpExist = this.snmptrapStrategyApi.isIpExist(syslogResBo);
				//如果IP不存在，则保存
				if (!isIpExist) {
					saveCount += this.snmptrapStrategyApi.saveIpAddress(syslogResBo);
				} else {
					return toFailForGroupNameExsit("保存失败,IP地址"+ ipArr[i] + "已经存在!");
				}
			}
		}
		return toSuccess(saveCount);
	}
}
